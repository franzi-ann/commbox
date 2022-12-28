package de.fomwebtech.s3;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;

//Ursprünglich wollten wir in ein S3 abspeichern - zu Demozewcken haben wir uns aber für die lokale Ablage entschieden.
/*Diese Klasse speichert und löscht Dateien in einem Amazon S3-Bucket. Der S3-Client wird über AWS-Zugangsdaten und den Endpunkt-URL 
 * für den Zugriff auf den S3-Bucket initialisiert. Die Methode saveFile nimmt einen Dateinamen und einen InputStream entgegen
 * und speichert die Datei im S3-Bucket. Die Methode delete löscht eine Datei aus dem S3-Bucket anhand des übergegebenen Dateinamen*/

public class S3PostboxWriter {
	
	
	public S3PostboxWriter() {
	
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	private String bucket = ConfigurationHolder3.getConfiguration().getString("postbox.s3.bucket","commbox");
	private String s3URL = ConfigurationHolder3.getConfiguration().getString("postbox.s3.url","https://s3-internal.amazon.de");
	private String accessKey = ConfigurationHolder3.getConfiguration().getString("postbox.s3.accessKey","TDCNCLW7GICXOR9JTNTM");
	private String secretKey = ConfigurationHolder3.getConfiguration().getString("postbox.s3.secretKey","qQ/BWyY5N+hmJeEagXOy+Euv9q6J1Z6IyivGDqP4");

	private AmazonS3 s3Client = null;
		 
	public void saveFile(String key, InputStream is) throws IOException, ApplicationException {
		
		logger.debug("Trying to save {} to S3 storage",key);
		
		AWSCredentials credentials = new BasicAWSCredentials(
			accessKey,
			secretKey
		);
		
		s3Client = AmazonS3ClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withPathStyleAccessEnabled(true)
              .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3URL, ""))
              .build();
      
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(is.available());

        PutObjectResult result = s3Client.putObject(bucket, key, is, metadata);
        logger.debug("File stored with eTag: {}",result.getETag());
        logger.debug("File stored with version: {}",result.getVersionId());

	}
	
	public void delete(String key) throws IOException, ApplicationException {
		
		logger.debug("Trying to delete {} from S3 storage",key);
		
		AWSCredentials credentials = new BasicAWSCredentials(
			accessKey,
			secretKey
		);
		
		s3Client = AmazonS3ClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withPathStyleAccessEnabled(true)
              .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3URL, ""))
              .build();
      
	    s3Client.deleteObject(bucket, key);

	}

}
