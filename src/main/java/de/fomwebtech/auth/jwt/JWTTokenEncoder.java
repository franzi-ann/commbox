package de.fomwebtech.auth.jwt;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.user.User;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;

public class JWTTokenEncoder {
	
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	public String getJWTToken(User user){
		
		logger.info("Trying to build JWT token for user {}",user.getEmail());
		
		String password = ConfigurationHolder3.getConfiguration().getString("appconfig.jwt.secretKey", "LJS37842983829!jls.!");
		
		int expirationTimeInMinutes = ConfigurationHolder3.getConfiguration().getInt("appconfig.jwt.expiration", 43200);
		
		
		Signer signer = HMACSigner.newSHA512Signer(password);

		// Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
		JWT jwt = new JWT().setIssuer("www.test.de")
		                   .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
		                   .setSubject("Validated user")
		                   .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(expirationTimeInMinutes));
		
		jwt.addClaim("user", user.toJSON().toString());
		
		return JWT.getEncoder().encode(jwt, signer);
		
		
	}
	

}
