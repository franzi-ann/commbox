package de.fomwebtech.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	
	public static String getRandomToken() {
		int length = 64;
		StringBuilder sb = new StringBuilder("");
		
		SecureRandom random = new SecureRandom();
		char[] symbols = new char[length];

		for (int idx = 0; idx < 16; ++idx)
		  symbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < length; ++idx)
		  symbols[idx] = (char) ('a' + idx - 10);
			
		char[] buf = new char[length];
		
		for (int idx = 0; idx < buf.length; ++idx) 
		      sb.append(Integer.toString(random.nextInt(symbols.length),32));	
		
		return sb.toString();
	}	


}
