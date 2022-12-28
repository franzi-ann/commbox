package de.fomwebtech.init.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.configuration.ConfigurationHolder3;

/*Diese Klasse lädt das Template für E-Mails, welches Benachrichtigungen über neue Nachrichten in einem Postfach versendet.*/
public class EmailTemplateLoader {
	
	private static EmailTemplateLoader instance;
	private static final Timer timer = new Timer();
	private static String NOTIFICATON_TEMPLATE = "";
	
/*Die Klasse enthält den Timer zum erneuten laden und das Template selbst.*/	
	public static EmailTemplateLoader getInstance() {
		if (instance==null)
			instance=new EmailTemplateLoader();
			
		return instance;
		
	}

	private EmailTemplateLoader() {
		start();
	}
	
	public String getEmail() {
		return NOTIFICATON_TEMPLATE;
	}
	
	public String getSubject() {
		return "Neue Nachricht in Deinem Postfach!";
	}
	
	private void start() {
		timer.schedule(new TimerTask () {

			@Override
			public void run() {
				ThreadedLoadTemplate job = new ThreadedLoadTemplate();
				NOTIFICATON_TEMPLATE = job.run();
			}			
			
		}, 0, 1800000L );	
	}

	public void destroy() {
		timer.cancel();
	}
}

/*Diese Klasse stellt eine Funktion zum Laden des Templates bereit. Das Template wird nach Ablauf des Timers neu geladen. 
 * Das File wird in einer bestimmten Zeichenkodierung (UTF-8) aus der Datei gelesen und als String zurückgegeben */
class ThreadedLoadTemplate  {
	
	private Logger logger = LogManager.getLogger(ThreadedLoadTemplate.class);	
	public ThreadedLoadTemplate() {
		// TODO Auto-generated constructor stub
	}

	public String run() {
		String file = ConfigurationHolder3.getConfiguration().getString("postbox.notification.template", null);
		if (file!=null) {
			logger.info("Loading template from file: {}",file);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)),"UTF-8"));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line=br.readLine())!=null) {
					sb.append(line);
				}
				br.close();
				return sb.toString();
			}
			catch (Exception e) {
				logger.error("",e);
			}
		}
		else {
			logger.error("No template file found");
		}

		return "";
		
	}

}