package de.fomwebtech.s3;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;


public class FilePostboxReader {
	
	
	public FilePostboxReader() {
	}

	private String filePath = ConfigurationHolder3.getConfiguration().getString("postbox.file.path","/var/tmp/postbox");

 
	public S3Object getFile(String objectName) throws IOException, ApplicationException {

		S3Object s3object = null;
		
		FileInputStream fis = new FileInputStream(filePath + "/" + objectName);
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fis.available());
        s3object = new S3Object();
        s3object.setObjectMetadata(metadata);
        if (fis.markSupported())
        	fis.reset();
        s3object.setObjectContent(new ByteArrayInputStream(fis.readAllBytes()));
        fis.close();
		return s3object;

	}

}
