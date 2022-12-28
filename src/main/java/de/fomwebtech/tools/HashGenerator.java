package de.fomwebtech.tools;

import java.math.BigInteger;
import java.security.MessageDigest;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*Diese Klasse generiert einen Hash eines gegeben Kennwortes. */
public class HashGenerator {
	
	private static Logger logger = LogManager.getLogger(HashGenerator.class);
	
	public static String createSHA1PasswordHash(String password) {
		String sha1 = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
	        digest.reset();
	        digest.update(password.getBytes("utf8"));
	        sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
		} catch (Exception e){
			logger.error("Could not create password hash",e);
		}
		return sha1;
	}

}
