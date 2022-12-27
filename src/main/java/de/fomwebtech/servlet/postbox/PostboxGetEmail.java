package de.fomwebtech.servlet.postbox;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.s3.model.S3Object;

import de.fomwebtech.database.statements.SQLStatementsPostbox;
import de.fomwebtech.email.EmailResponseParser;
import de.fomwebtech.s3.FilePostboxReader;
import de.fomwebtech.s3.S3PostboxReader;
import de.fomwebtech.user.User;



@WebServlet(urlPatterns = ("/service/postboxEmail"))
public class PostboxGetEmail extends HttpServlet {
    private static final long serialVersionUID = 5652268371771279360L;
    private Logger logger = LogManager.getLogger(getClass());

    public void service(HttpServletRequest request, HttpServletResponse response) {

        try {

        	String filename = request.getParameter("filename");
        	User user = (User)request.getAttribute("user");
            SQLStatementsPostbox sql = new SQLStatementsPostbox();
            if (filename != null && sql.validateGetFile(user.getEmail(),filename)) {
            	//S3PostboxReader reader = new S3PostboxReader();
            	FilePostboxReader reader = new FilePostboxReader();
            	S3Object s3Object = reader.getFile(filename);
            	
        		if (s3Object == null) {
    				response.sendError(HttpServletResponse.SC_NOT_FOUND);
    				return;
    			}
        		
        		EmailResponseParser erp = new EmailResponseParser();
        		JSONObject json = erp.parse(s3Object.getObjectContent());
    		    response.setHeader("Content-Type","application/json");
    		    PrintWriter wr = response.getWriter();
        		wr.write(json.toString());
    			wr.close();
    		    
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

