package wibd.ls.ml.recognition.batch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import wibd.ls.ml.util.AWSClient;
import wibd.ls.ml.util.EnvironmentConstants;

public class RekognitionTextExtraction {

	BasicAWSCredentials credentials = new BasicAWSCredentials(EnvironmentConstants.accessKeyId, EnvironmentConstants.secretKeyId);
	AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(EnvironmentConstants.region)
			.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.US_EAST_2).build();

	private AWSClient awsClient;
	public RekognitionTextExtraction() {
		awsClient = new AWSClient();
	}
	public void extract(){
		awsClient.extractText(EnvironmentConstants.BUCKET_NAME, s3Client,rekognitionClient, credentials);
	}
	
	public static void main(String[] args) {
		RekognitionTextExtraction textExtraction = new RekognitionTextExtraction();
		textExtraction.extract();
	}
}
