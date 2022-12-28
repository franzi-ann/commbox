package de.fomwebtech.init.jobs;



import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.logging.log4j.core.config.Configurator;

/*Diese Klasse initialisiert das Logging-Framework Log4j. Zuert wird geprüft, ob die Property log4j.configurationFile gesetzt ist. 
 * Falls ja, wird diese verwendet. Falls nicht wird log4j.xml aus dem War-File verwendet. Die Methode Configurator.initialize lädt diese dann. */

public class InitializeLog4j
{

	public void init() {
		
		try {
			
			String fname = System.getProperty("log4j.configurationFile");
			
			if (fname==null) 
			{
				Logger.getAnonymousLogger().info("Could not find -D property log4j.configuration using log4j.xml from war file");
				URI log4JConfig = InitializeLog4j.class.getResource("log4j.xml").toURI();
           		Configurator.initialize("Log4j2config", null,log4JConfig);

           	}
                
        } 
		catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println("Error reading log4.xml");
			e.printStackTrace();
		}
		
	}

}