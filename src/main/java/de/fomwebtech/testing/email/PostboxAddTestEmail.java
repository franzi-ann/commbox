package de.fomwebtech.testing.email;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.user.User;


@WebServlet(urlPatterns = ("/service/addTestEmail"))
public class PostboxAddTestEmail extends HttpServlet {
    private static final long serialVersionUID = 5652268371771279360L;
    private Logger logger = LogManager.getLogger(getClass());

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

        	User user = (User)request.getAttribute("user");
        	String type = request.getParameter("type");
        	
        	if (type==null || !type.matches("\\d")) {
        		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        		return;
        	}
        	
        	int emailType = Integer.parseInt(type);
        	if (emailType<0 || emailType>2) {
        		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        		return;
        	}
        	
        	PostboxTest postboxTest = new PostboxTest();
        	postboxTest.email(user.getEmail(), emailType);
        }
        catch (Exception e) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("", e);
        }
    }

}

