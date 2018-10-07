package wibd.ls.ml.recognition.lambda.handler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;

import wibd.ls.ml.util.AWSClient;
import wibd.ls.ml.util.EnvironmentConstants;
import wibd.ls.ml.util.TextExtraction;

public class RekognitionHandler implements RequestHandler<S3Event, String> {
	AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(EnvironmentConstants.region).build();
	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
	TextExtraction textExtract = new TextExtraction();
	AWSClient awsClient = new AWSClient();
	public String handleRequest(S3Event input, Context context) {
		String alltext = "";
		LambdaLogger logger = context.getLogger();
		for (S3EventNotification.S3EventNotificationRecord record : input.getRecords()) {

			String s3Key = record.getS3().getObject().getKey();
			String s3Bucket = record.getS3().getBucket().getName();

			logger.log("Received record with bucket: {}  and key:  {}" + s3Bucket + " - " + s3Key);
//			rekognizeImage(s3Key, s3Bucket, logger) ;
			awsClient.rekognizeImage(s3Key, s3Bucket, rekognitionClient, null);
		}
		return alltext;
	}

}
