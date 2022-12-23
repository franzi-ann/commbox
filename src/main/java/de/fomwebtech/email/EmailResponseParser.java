package de.fomwebtech.email;

import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.mail.util.BASE64DecoderStream;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailResponseParser {
	
	private Logger logger = LogManager.getLogger(this.getClass());

	public JSONObject parse(InputStream is) {
		JSONObject response = new JSONObject();
		JSONArray jar = new JSONArray();
		try {
			Session s = Session.getInstance(new Properties());
			MimeMessage mimeMessage = new MimeMessage(s, is);
			response.put("subject", mimeMessage.getSubject());
			response.put("received_date", mimeMessage.getReceivedDate());
			
			if (mimeMessage.getContentType().startsWith("text")) {
				try {
					JSONObject json = new JSONObject();
					json.put("content",(String)mimeMessage.getContent());
					json.put("content-type",mimeMessage.getContentType());
					jar.put(json);
				}
				catch (Exception e) {
					logger.warn("Exception: ",e);
				}
			}
			else {		
				try {
					MimeMultipart mp = (MimeMultipart)mimeMessage.getContent();
					for (int i=0;i<mp.getCount();i++) {
						JSONObject json = new JSONObject();
						if (mp.getBodyPart(i).getContentType().indexOf("text")>-1) {
							json.put("content",(String)mp.getBodyPart(i).getContent());
							json.put("content-type",mp.getBodyPart(i).getContentType());
						}
						else {
							BASE64DecoderStream ds = (BASE64DecoderStream)(mp.getBodyPart(i).getContent());
							String content = Base64.getEncoder().encodeToString(ds.readAllBytes());
							json.put("content",content);
							json.put("content-type",mp.getBodyPart(i).getContentType());
						}
	
						jar.put(json);
					}
				}
				catch (Exception e) {
					logger.warn("Exception: ",e);
				}
			}

			
		}
		catch (Exception e) {
			logger.error("",e);
		}
		response.put("content", jar);
		return response;
	}

}
