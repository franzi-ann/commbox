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

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)  {

		HttpServletRequest request = (HttpServletRequest) req;
	    HttpServletResponse response = (HttpServletResponse) res;

		try {
			logger.debug("Filtering request");

		    String token = request.getHeader("token");
  
		    //Check for registration and other urls which need to be accessed without token
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
		    		authResponse = auth.get(token);
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
