package de.fomwebtech.communication;

import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.ApplicationException;
import de.fomwebtech.init.jobs.EmailTemplateLoader;



/* Diese Klasse erstellt Emails und leitet sie an das konfigurierte SMTP Gateway. Hierfür werden verschiedene Einstellungen geladen:
 * E-MAil Absender, Host-Server, Portnummer und die Authentifizierung.  */

public class MailSender {
	
	private String sender = ConfigurationHolder3.getConfiguration().getString("appconfig.mail.sender", "no-reply@congstar.net");
	private String smtpHost = ConfigurationHolder3.getConfiguration().getString("appconfig.mail.smtp.host", "172.25.0.14");
	private String smtpTLS = ConfigurationHolder3.getConfiguration().getString("appconfig.mail.smtp.starttls.enable","false");
	private int smtpPort = ConfigurationHolder3.getConfiguration().getInt("appconfig.mail.smtp.port",2501);
	private int connectionTimeout = ConfigurationHolder3.getConfiguration().getInteger("appconfig.mail.smtp.connetiontimeout", 5000);
	private int timeout = ConfigurationHolder3.getConfiguration().getInteger("appconfig.mail.smtp.timeout", 10000);
	private String smtpUser = ConfigurationHolder3.getConfiguration().getString("appconfig.mail.smtp.user", "test");
	private String smtpPassword = ConfigurationHolder3.getConfiguration().getString("appconfig.mail.smtp.password", "test");
	private static Logger logger = LogManager.getLogger(MailSender.class);

/*Diese Methode sendet die Email an den Empfänger. Es wird ein Properties Objekt erstellt, welches Einstellungen für den EMailversand enthält. 
 * Das Session Objekt wird ebenfalls für den EMail Versand verwendet. Das Mime Message Objekt enthält die Email Nachricht. Die EMail wird dann
 * mithilfe des Transportobjektes versendet. Das Transportobjekt wird importiert (jakarta.mail.Transport)  */
	
	public void email(String recipient) throws ApplicationException 
	{	
				
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", smtpPort);
			if ("true".equals(smtpTLS)) {
				props.put("mail.smtp.starttls.enable", smtpTLS);
			}
			Session s;
			if ("true".equals(smtpTLS)) {
				logger.debug("Starting TLS session with Mailserver: {} on port: {}",smtpHost,smtpPort);
		        props.put("mail.smtp.starttls.enable","true");
		        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtps.timeout", timeout);
				props.put("mail.smtps.connectiontimeout", connectionTimeout);
			}
			else {
		        props.put("mail.smtp.starttls.enable","false");
				props.put("mail.smtp.timeout", timeout);
				props.put("mail.smtp.connectiontimeout", connectionTimeout);
				
			}
			if (ConfigurationHolder3.getConfiguration().getBoolean("appconfig.mail.smtp.authenticate", false)) 
			{
				props.put("mail.smtp.auth", "true");
				logger.debug("Authentication required for Mailserver. Using user {}",smtpUser);
				s = Session.getInstance(props,new MailAuthenticator(smtpUser,smtpPassword));
			}
			else {
				 s = Session.getInstance(props,null);
			}
			MimeMessage message = new MimeMessage(s);
			InternetAddress from = new InternetAddress(sender);
			message.setFrom(from);
			final InternetAddress to = new InternetAddress(recipient);
			message.addRecipient(MimeMessage.RecipientType.TO, to);

			message.setSubject(EmailTemplateLoader.getInstance().getSubject());
			message.setContent(EmailTemplateLoader.getInstance().getEmail(), "text/html;charset=utf-8");
			
			Transport.send(message);
		}
		
		catch (AddressException e) {
			// TODO Auto-generated catch block
			logger.error("Address field invalid.",e);
			throw new ApplicationException("Beim Versenden der Email ist ein Fehler aufgetreten");
		} 
		catch (MessagingException e) {
			// TODO Auto-generated catch block
			logger.error("Error sending email", e);
			throw new ApplicationException("Beim Versenden der Email ist ein Fehler aufgetreten");
		}
	}

}

