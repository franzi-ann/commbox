package de.fomwebtech.servlet.postbox;

import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import de.fomwebtech.database.statements.SQLStatementsPostbox;
import de.fomwebtech.user.User;



@WebServlet(urlPatterns = ("/service/postboxEntities"))
public class PostboxGetEntities extends HttpServlet {
    private static final long serialVersionUID = 5652268371771279360L;
    private Logger logger = LogManager.getLogger(getClass());

    public void service(HttpServletRequest request, HttpServletResponse response) {

        try {

        	User user = (User)request.getAttribute("user");
            SQLStatementsPostbox sql = new SQLStatementsPostbox();
            JSONArray jar = sql.getEntries(user.getEmail());
                
            PrintWriter wr = response.getWriter();
            wr.write(jar.toString());
            wr.close();
            
        }
        catch (Exception e) {
            logger.error("", e);
        }
    }

}
