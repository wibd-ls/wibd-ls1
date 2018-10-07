package wibd.ls.ml.util;

import java.util.List;
import java.util.TreeMap;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import wibd.ls.ml.db.ImageErrorReport;
import wibd.ls.ml.db.PatentedImage;

public class AWSClient {
	private TextExtraction textExtract;
	public AWSClient() {
		textExtract = new TextExtraction();
	}
	public void extractText(String bucketName, AmazonS3 s3Client, AmazonRekognition rekognitionClient, BasicAWSCredentials credentials) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);

		ObjectListing objects = s3Client.listObjects(listObjectsRequest);
		for (;;) {
			List<S3ObjectSummary> summaries = objects.getObjectSummaries();
			if (summaries.size() < 1) {
				break;
			}
			summaries.forEach(s -> rekognizeImage(s.getKey(), bucketName, rekognitionClient, credentials));
			objects = s3Client.listNextBatchOfObjects(objects);
		}
	}

	public void rekognizeImage(String s3Key, String s3Bucket, AmazonRekognition rekognitionClient, BasicAWSCredentials credentials) {
		DetectTextRequest request = new DetectTextRequest().withImage(new Image().withS3Object(
				new com.amazonaws.services.rekognition.model.S3Object().withName(s3Key).withBucket(s3Bucket)));
		try {
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			
			TreeMap<String, List<Integer>> numMap = textExtract.computeTextpositionMap(textDetections);
			Integer year = textExtract.getYear(numMap);
			Integer patentNumber = textExtract.getPatent(numMap);
			
			System.out.println("Year of Patent is " + year);
			System.out.println("Patent number is " + patentNumber);
			System.out.println("############################################### key is "+s3Key);
			if (year != null && patentNumber != null) {
				insertToPatentTable(year, patentNumber, s3Bucket + "/" + s3Key, credentials);
				System.out.println("Year of Patent is " + year);
				System.out.println("Patent number is " + patentNumber);
			} else {
				System.out.println("###################INSERTING TO ERROR REPORT ############################");
				System.out.println("Year of Patent is " + year);
				System.out.println("Patent number is " + patentNumber);
				if (year == null) {
					year = -1;
				}
				if (patentNumber == null) {
					patentNumber = -1;
				}
				insertToErrorTable(year, patentNumber, s3Bucket + "/" + s3Key, credentials );
			}
		} catch (AmazonRekognitionException e) {
			System.out.println("============================== image failed to rekognize " + s3Key);
//			insertToErrorTable(-1, -1, s3Bucket + "/" + s3Key);
			e.printStackTrace();
		}
	}

	private void insertToPatentTable(Integer year, Integer patent, String imagePath, BasicAWSCredentials credentials ) {
		
		AmazonDynamoDB client = null;
		if(credentials == null) {
			client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
		}else {
			client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2)
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		PatentedImage patImg = mapper.load(PatentedImage.class, patent, year);
		PatentedImage item = null;
		if(patImg != null) {
			item = patImg;
		}else {
			item = new PatentedImage();
			item.setYear(year);
			item.setPatent(patent);
			if (year.equals(patent)) {
				item.setSame_patent_n_year("YES");
			} else {
				item.setSame_patent_n_year("NO");
			}
		}
		item.setEmptyImagePath(imagePath);
		
		mapper.save(item);
	}

	private void insertToErrorTable(Integer year, Integer patent, String imagePath, BasicAWSCredentials credentials) {
		AmazonDynamoDB client = null;
		if(credentials == null) {
			client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
		}else {
			client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2)
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
		DynamoDBMapper mapper = new DynamoDBMapper(client);

		ImageErrorReport item = new ImageErrorReport();
		item.setYear(year);
		item.setPatent(patent);
		item.setImage_path(imagePath);
		mapper.save(item);
	}

}
