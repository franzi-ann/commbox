package de.fomwebtech.s3;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;

//Ursprünglich wollten wir in ein S3 abspeichern - zu Demozwecken haben wir uns aber für die lokale Ablage entschieden.
/*Diese Klasse dient dazu, eine Datei aus einem Amazon S3-Bucket zu lesen. Die Klasse nutzt das Amazon S3-API und die AWS SDK für Java,
 * um die Verbindung zum S3-Dienst herzustellen und die Datei herunterzuladen. */
public class S3PostboxReader {
	
	
	public S3PostboxReader() {
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	private String bucket = ConfigurationHolder3.getConfiguration().getString("postbox.s3.bucket","commbox");
	private String s3URL = ConfigurationHolder3.getConfiguration().getString("postbox.s3.url","https://s3-internal.amazon.de");
	private String accessKey = ConfigurationHolder3.getConfiguration().getString("postbox.s3.accessKey","TDCNCLW7GICXOR9JTNTM");
	private String secretKey = ConfigurationHolder3.getConfiguration().getString("postbox.s3.secretKey","qQ/BWyY5N+hmJeEagXOy+Euv9q6J1Z6IyivGDqP4");

	private AmazonS3 s3Client = null;
	
	 
	public S3Object getFile(String objectName) throws IOException, ApplicationException {
	

		S3Object s3object = null;
		
		AWSCredentials credentials = new BasicAWSCredentials(
			accessKey,
			secretKey
		);
		s3Client = AmazonS3ClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withPathStyleAccessEnabled(true)
              .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3URL, ""))
              .build();
      
		
		try {
			s3object = s3Client.getObject(bucket, objectName);
			
		}
		catch (AmazonServiceException e) {
			logger.error("Could not fetch document",e);
		}
		catch (Exception e) {
			logger.error("Error fetching document",e);
		}
		return s3object;

	}

}
