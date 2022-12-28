package de.fomwebtech.s3;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;

/*Diese Klasse dient dazu, Dateien aus einer lokalen Dateiablage zu lesen.*/
public class FilePostboxReader {
	
	
	public FilePostboxReader() {
	}

	private String filePath = ConfigurationHolder3.getConfiguration().getString("postbox.file.path","/var/tmp/postbox");

/*Diese Methode liest eine Datei aus dem angegebenen Pfad und gibt die als S3Objekt zurück.
 * Hierfür wird ein FileInputStream und ein ObjectMetadata-Objekt erstellt und befüllt.  Dann wird ein S3Object erstellt und die Metadaten
 * gesetzt. Dann wird der FileInputStream in einen ByteArrayInputStream umgewandelt und dem S3Object als Inhalt zugewiesen.*/ 
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
