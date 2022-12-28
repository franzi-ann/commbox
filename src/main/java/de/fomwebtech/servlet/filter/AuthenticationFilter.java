package de.fomwebtech.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.auth.AuthProvider;
import de.fomwebtech.auth.AuthenticationResponse;
import de.fomwebtech.exception.AuthenticationException;
import de.fomwebtech.user.User;

/*Diese Klasse dient der Überprüfung der Authentifizierung von Anfragen an die Web-Anwendung. Sie implementiert das Filter Interface,
 *welches Teil des des Java Servlet APIs ist. Es ist eine Zwischenkomponente, welches zwischen dem Client und dem Servlet platziert wird 
 *und es ermöglicht, HTTP-Anfragen und -Antworten zu manipulieren, bevor sie das Servlet erreichen oder von dort zurück zum Client gesendet werden */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	private ServletContext context;
	private Logger logger = LogManager.getLogger(this.getClass());
	private static final HashSet<String> excludedURLs = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
	{
		add("/commbox/management/info");
		add("/commbox/api/incomingEMail");
	}};

	
	public void init(FilterConfig fConfig) throws ServletException {
		this.context = fConfig.getServletContext();
		this.context.log("AuthenticationFilter initialized");
	}

/*Diese Methode dient dazu, sicherzustellen, dass Anfragen von authentifizierten Benutzern stammen. Der Token aus dem HTTP-Header wird gelesen. 
 * Ebenfalls wird geprüft ob sich die URL der Anfrage in der Liste befindet. Falls ja, wird die Anfrage unverändert durchgereicht.
 * Falls die URL nicht ausgeschlossen ist, wird überprüft, ob ein Token existiert und ob die URL der Anfrage nicht die URL für den Login ist.
 * Wenn beides zutrifft, wird der Token überprüft und der Benutzer wird authentifiziert. 
 * Wenn der Token gültig ist, wird die Anfrage durchgereicht und der User an die Anfrage angehängt. 
 * Wenn der Token nicht gültig ist, wird ein Fehlercode zurück*/
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)  {

		HttpServletRequest request = (HttpServletRequest) req;
	    HttpServletResponse response = (HttpServletResponse) res;

		try {
			logger.debug("Filtering request");

		    String token = request.getHeader("token");
  
		    //Überprüfe Registrierung und andere URLs, die ohne Token zugänglich sein müssen
		    if (excludedURLs.contains(request.getRequestURI())) {
		    	chain.doFilter(req, res);
		    	return;
		    }
		    else if (token!=null && !request.getRequestURI().matches("/commbox/login")) {
		    	AuthProvider auth = new AuthProvider();
		    	logger.debug("Checking token: " + token);
		    	try {
		    		User user = auth.checkJWTToken(token);
		    		request.setAttribute("user", user);
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	catch (AuthenticationException e) {
					// TODO: handle exception
		    		logger.debug("Token is not valid anymore");
		    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    		return;
				}
		    }
		    else if (token==null && !request.getRequestURI().matches("/commbox/login")) {
		    	logger.debug("No token in request");
		    	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    	return;
		    	
		    }
		    else {
		    	AuthenticationResponse authResponse = null;
		    	logger.debug("Login request detected on URI {}",request.getRequestURI());
		    	AuthProvider auth = new AuthProvider();
		    	if (request.getMethod().matches("POST")) {
		    		authResponse = auth.post(request);
		    	}
		    	else {
		    		authResponse = auth.validateToken(token);
		    	}
		    	
		    	response.setContentType("application/json");
		    	response.setStatus(authResponse.getHttpStatus());
				PrintWriter wr = response.getWriter();
				wr.write(authResponse.getJson().toString());
				wr.flush();
				wr.close();
				return;
		    }
			
		}
		catch (Exception e) {
			logger.error("An error was thrown",e);
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} 
			catch (IOException e1) {
				//ignore
			}
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

		
}
