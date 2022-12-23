package de.fomwebtech.auth.jwt;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import de.fomwebtech.configuration.ConfigurationHolder3;


public class EmailServiceJWTDecoder {

		private Logger logger = LogManager.getLogger(this.getClass());
		

		public boolean validate(String user, String token) {
			
			if (user==null || token == null) 
				return false; 
			
			JSONObject json = decryptor(user,token);
			
			return json!=null;
		}

		
		private JSONObject decryptor (String username, String encodedJWT) 
		{
			
			JSONObject json = null;
			String secretString = ConfigurationHolder3.getConfiguration().getString("appconfig.security.jwt.secretKeys."+username,null);

			if (secretString==null) {
				logger.error("Secret or JWT Token for user: {} may not be null",username);
				return null;
			}
			try {
				Verifier verifier = HMACVerifier.newVerifier(secretString);
				JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
				if (jwt.isExpired()) {
					logger.error("Token {} is expired",encodedJWT);
					return null;
				}
				json = new JSONObject(jwt.getObject("user").toString());
				logger.debug("Token for user: {} expires at {}",json.getString("name"),jwt.expiration);
			}
			catch (Exception e) {
				logger.warn("Error while decoding token",e);
			}
				
			return json;
			
		}

}
