package de.fomwebtech.init;

import java.util.logging.Logger;

import de.fomwebtech.database.connectors.HikariDataSourceManager;
import de.fomwebtech.init.jobs.EmailTemplateLoader;
import de.fomwebtech.init.jobs.InitializeLog4j;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


@WebListener
public class InitializationServlet implements ServletContextListener {

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
	
	public void contextDestroyed(ServletContextEvent sce) {
		HikariDataSourceManager.shutDownPools();
		EmailTemplateLoader.getInstance().destroy();
	}
	
}
