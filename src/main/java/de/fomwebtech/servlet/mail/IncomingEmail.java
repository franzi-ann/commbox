package de.fomwebtech.servlet.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.auth.jwt.EmailServiceJWTDecoder;
import de.fomwebtech.email.EmailProcessor;
import de.fomwebtech.exception.ConstraintViolationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/api/incomingEMail")
public class IncomingEmail extends HttpServlet {

	private static final long serialVersionUID = 1717947941937912111L;
	private Logger logger = LogManager.getLogger(this.getClass());

	
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		
		try {
			
			String user = request.getHeader("user");
			String token = request.getHeader("token");
			EmailServiceJWTDecoder decoder = new EmailServiceJWTDecoder();
			if (!decoder.validate(user, token)) {
				logger.warn("Unauthorized use of servlet");
			}
			
			String line;
			logger.debug("Start processing input");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			while ((line=br.readLine())!=null) {
				sb.append(line);
			}
			br.close();
			
			if (sb.length() > 0) {
				EmailProcessor ep= new EmailProcessor(); 
				ep.run(URLDecoder.decode(sb.toString().startsWith("email=")?sb.toString().replaceFirst("email=", ""):sb.toString(), "UTF-8"));
				logger.debug("Input processed");
			}
			else {
				logger.warn("No email found in request");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}

		}
		catch (ConstraintViolationException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
	}
	

	public void doGet (HttpServletRequest request, HttpServletResponse response) 
	{
		
		try {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		catch (Exception e) {
			logger.error("",e);
		}
		
	}

	

}
