package de.fomwebtech.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;



public class FilePostboxWriter {
	
/*Diese Klasse ist für das Speichern und Löschen von Dateien im lokalen Dateisystem verantwortlich.. */	
	public FilePostboxWriter() {
	
	}

	private Logger logger = LogManager.getLogger(this.getClass());
	private String filePath = ConfigurationHolder3.getConfiguration().getString("postbox.file.path","/var/tmp/postbox");

//Die Methode speichert eine Datei im lokalen Dateisystem und benennt sie mit dem angegebenen Namen.		 
	public void saveFile(String key, InputStream is) throws IOException, ApplicationException {
		
		logger.debug("Trying to save {} to S3 storage",key);
		FileOutputStream fos = new FileOutputStream(filePath + "/" + key);
		fos.write(is.readAllBytes());
		fos.close();

	}

// Diese Methode löscht eine Datei mit dem angegebenen Namen aus dem lokalen Dateisystem
	
	public void delete(String key) throws IOException, ApplicationException {
		
		logger.debug("Trying to delete {} from S3 storage",key);
		
		File f = new File(filePath + "/" + key);
		f.delete();
	}

}
