package de.fomwebtech.servlet.postbox;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.s3.model.S3Object;

import de.fomwebtech.database.statements.SQLStatementsPostbox;
import de.fomwebtech.s3.S3PostboxReader;
import de.fomwebtech.user.User;



@WebServlet(urlPatterns = ("/service/postboxFile"))
public class PostboxGetFile extends HttpServlet {
    private static final long serialVersionUID = 5652268371771279360L;
    private Logger logger = LogManager.getLogger(getClass());

    public void service(HttpServletRequest request, HttpServletResponse response) {

        try {

        	User user = (User)request.getAttribute("user");
        	String filename = request.getParameter("filename");
            SQLStatementsPostbox sql = new SQLStatementsPostbox();
            if (filename != null && sql.validateGetFile(user.getEmail(),filename)) {
            	S3PostboxReader reader = new S3PostboxReader();
            	S3Object s3Object = reader.getFile(filename);
            	
        		if (s3Object == null) {
    				response.sendError(HttpServletResponse.SC_NOT_FOUND);
    				return;
    			}
        		response.getOutputStream().write(s3Object.getObjectContent().readAllBytes());
    			
    			response.setContentLength((int) s3Object.getObjectMetadata().getContentLength());
    		    response.setHeader("Content-Disposition", "attachment; filename = " + filename);
    		    response.flushBuffer();
            }
            else {
            	response.sendError(HttpServletResponse.SC_NOT_FOUND);
            	return;
            }
        	
            
        }
        catch (Exception e) {
            logger.error("", e);
        }
    }

}
