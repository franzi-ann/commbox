package de.fomwebtech.testing.email;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.user.User;

/*Diese Klasse dienst als Servlet für die Generierung der Testmail.*/
@WebServlet(urlPatterns = ("/service/addTestEmail"))
public class PostboxAddTestEmail extends HttpServlet {
    private static final long serialVersionUID = 5652268371771279360L;
    private Logger logger = LogManager.getLogger(getClass());

/*Diese Methode erhält 2 Übergabeparameter: request und response. Zuerst wird ein Userobjekt und type-Parameter generiert. 
 * Danach wird das type-Parameter überprüft. Wenn das type-Parameter nicht null ist und mit dem regulären Ausdruck übereinstimmt, 
 * wird es als emailType zugewiesen. Der Servlet prüft dann, ob emailType zwischen 0 und 2 liegt. Falls nein, wird eine HTTP-Fehlerantwort 
 * zurückgegeben. Falls ja, erstellt der Servlet eine Instanz der Klasse PostboxTest und ruft deren email-Methode 
 * mit der E-Mail-Adresse des user und emailType als Argumenten auf.*/
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

