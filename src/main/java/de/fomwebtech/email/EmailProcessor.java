package de.fomwebtech.email;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.communication.MailSender;
import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.database.statements.SQLStatementsPostbox;
import de.fomwebtech.exception.ApplicationException;
import de.fomwebtech.exception.ConstraintViolationException;
import de.fomwebtech.s3.S3PostboxWriter;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailProcessor {
	
	private Logger logger = LogManager.getLogger(this.getClass());
	private String notificationHeaderName = ConfigurationHolder3.getConfiguration().getString("postbox.header.notification", "X-Notify");
	private final int DOCUMENT_TYPE_ID = 2;
	
	public void run(String message) throws Exception {
		
		Session s = Session.getInstance(new Properties());
		InputStream is = new ByteArrayInputStream(message.getBytes());
		MimeMessage mimeMessage = new MimeMessage(s, is);
		Address [] ias = mimeMessage.getAllRecipients();
		String filename = "message-" + mimeMessage.getMessageID().replaceAll("<", "").replaceAll(">", "") + "-"+ System.currentTimeMillis() + ".msg";
		FileMetaData metaData = new FileMetaData(filename, DOCUMENT_TYPE_ID);
		metaData.setCustomerEmail(ias[0].toString());
		metaData.setSubject(mimeMessage.getSubject());
		metaData.setDocumentDateEpochMS(mimeMessage.getReceivedDate()!=null?mimeMessage.getReceivedDate().getTime():System.currentTimeMillis());
		if (mimeMessage.getContentType().startsWith("text")) {
			try {
				metaData.setPreviewContent( ((String)mimeMessage.getContent()).substring(0, 80));
			}
			catch (Exception e) {
				logger.warn("Exception: ",e);
			}
		}
		else {	
			try {
				MimeMultipart mp = (MimeMultipart)mimeMessage.getContent();
				for (int i=0;i<mp.getCount();i++) {
					if (mp.getContentType().startsWith("text"))
						metaData.setPreviewContent( ((String)mimeMessage.getContent()).substring(0, 80));
				}
			}
			catch (Exception e) {
				logger.warn("Exception: ",e);
			}
		}

		if (is.markSupported()) {
			is.reset();
		}
		else {
			logger.warn("Marking InputStream is not supported, creating new one");
			is = new ByteArrayInputStream(message.getBytes());
		}
		
		if (!metaData.isValidConstraint())
			throw new ConstraintViolationException();
		
		SQLStatementsPostbox sql = new SQLStatementsPostbox();
		sql.addEntry(metaData);
		
		try {
			S3PostboxWriter writer = new S3PostboxWriter();
			writer.saveFile(filename, is);
		}
		catch (Exception e) {
			sql.removeEntry(metaData);
			throw new ApplicationException("Could not add file to S3");
		}
		try {
			if (mimeMessage.getHeader(notificationHeaderName)!=null &&	Boolean.parseBoolean(mimeMessage.getHeader(notificationHeaderName)[0].trim()))
			{
				MailSender email = new MailSender();
				email.email(ias[0].toString());
			}				
		}
		catch (Exception e) {
			//ignore
		}
	}

}
