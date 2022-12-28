package de.fomwebtech.auth.jwt;

import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import de.fomwebtech.configuration.ConfigurationHolder3;
import de.fomwebtech.exception.AuthenticationException;
import de.fomwebtech.user.User;

/*Diese Klasse stellt sicher, dass ein User, der sich einloggt authentifiziert wird. Hierfür wird das jwt token entschlüsselt und bei erfolgreichem
 * entschlüsseln ein Userobjekt zurückgegeben. */

public class JWTTokenDecoder  {

	private User user = null;
	private Logger logger = LogManager.getLogger(this.getClass());

	public User validate(String encodedJWT) throws AuthenticationException {

		JSONObject json = decryptor(encodedJWT);
		
		if (json==null)
			throw new AuthenticationException("Unauthorized");
			user = new User(json.getString("email"));
			user.setFirstName(json.getString("first_name"));
			user.setLastName(user.getLastName());
			user.setUsername(user.getUsername());
			return user;
	}

/*Diese Methode entschlüsselt das JWT Token mit dem hinterlegten secret key. Falls keiner hinterlegt ist wird in alternativer Key übergeben */	
	private JSONObject decryptor (String encodedJWT) 
	{
		
		JSONObject json = null;
		String secretString = ConfigurationHolder3.getConfiguration().getString("appconfig.jwt.secretKey","LJS37842983829!jls.!");
		
		try {
				
			Verifier verifier = HMACVerifier.newVerifier(secretString);
			JWT jwt = JWT.getDecoder().decode(encodedJWT, verifier);
			json = new JSONObject(jwt.getObject("user").toString());
		}
		catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}
			
		return json;
		
	}

	


}
