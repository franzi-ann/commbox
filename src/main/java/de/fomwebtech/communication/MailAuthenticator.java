package de.fomwebtech.communication;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

/*Diese Klasse dient der Authentifizierung beim Senden einer Email. Sie ist eine Unterklasse von Authenticator, die von der JavaMail API verwendet wird.
 * Die Variablen username und password werden gesetzt. Diese sollen genutzt werden um die Email zu versenden.*/
public class MailAuthenticator extends Authenticator {
	
	private String username;
	private String password;
	
	public MailAuthenticator (String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public PasswordAuthentication getPasswordAuthentication () {
		return new PasswordAuthentication(username,password);
		
	}

}