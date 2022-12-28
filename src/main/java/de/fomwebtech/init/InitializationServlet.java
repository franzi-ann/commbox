package de.fomwebtech.init;

import java.util.logging.Logger;

import de.fomwebtech.database.connectors.HikariDataSourceManager;
import de.fomwebtech.init.jobs.EmailTemplateLoader;
import de.fomwebtech.init.jobs.InitializeLog4j;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/*Diese Klasse leitet von der Oberklasse ServletContextListener ab, welches Callback-Methoden bereitstellt, 
 * die von einem Servlet-Container aufgerufen werden wenn das Objekt, das die Web-Anwendung repräsentiert, initialisiert bzw. beendet wird.  */
@WebListener
public class InitializationServlet implements ServletContextListener {

/*Diese Methode wird bei Start der Webanwendung aufgerufen. initialisiert das Logging-Framework Log4j, 
 * initialisiert Hikari-DataSource-Pools und lädt Emailtemplates */
	public void contextInitialized(ServletContextEvent sce) {
		
		System.setProperty("mail.mime.base64.ignoreerrors", "true");
		Logger.getAnonymousLogger().info("Starting initialization process");

		Logger.getAnonymousLogger().info("Initializing logging framework log4j");
		InitializeLog4j log4j = new InitializeLog4j();
		log4j.init();
		Logger.getAnonymousLogger().info("Initialized logging framework log4j");	
		
		Logger.getAnonymousLogger().info("Initializing Hikari DataSources");
		HikariDataSourceManager.initializePools();
		Logger.getAnonymousLogger().info("Initialized Hikari DataSources");	
		
		Logger.getAnonymousLogger().info("Initializing Emailtemplate");
		EmailTemplateLoader.getInstance();
		Logger.getAnonymousLogger().info("Initialized Emailtemplate");	

		Logger.getAnonymousLogger().info("Finished initialization process");
	
	}
/*Diese Methode wird beim Beenden der Webanwendung aufgerufen. 
 * Die Methode schließt die Hikari-DataSource-Pools und zerstört das Emailtemplate-Objekt. */

	public void contextDestroyed(ServletContextEvent sce) {
		HikariDataSourceManager.shutDownPools();
		EmailTemplateLoader.getInstance().destroy();
	}
	
}
