package de.fomwebtech.configuration;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.ReloadingDetectorFactory;
import org.apache.commons.configuration2.builder.combined.ReloadingCombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/*Diese Klasse lädt verschiedene Einstellungen aus der Konfigurationsdatei.*/
public class ConfigurationHolder3 {
	
	private static final int RELOAD_NOT_REQUIRED=0;
	private static final int RELOAD_REQUIRED=1;
	private static final int RELOAD_FULLY_REQUIRED=2;
	
	private static BaseHierarchicalConfiguration conf = null;
	private static ConfigurationBuilder<? extends BaseHierarchicalConfiguration> builder2 = null;
	private static PeriodicReloadingTrigger trigger = null;
	static Logger logger = LogManager.getLogger(ConfigurationHolder3.class);
	private static volatile int reloadRequired = RELOAD_NOT_REQUIRED;
	
	private static String configPattern = System.getProperty("ApplicationConfig");
	
	static {
		new ConfigurationHolder3();
	}

	private ConfigurationHolder3() {
		init();
	}

	static void setReloadingRequired(int b) {
		reloadRequired = b;
	}
	
	private static void installReloadTrigger (ReloadingController controller)
	{
		if (controller!=null) {
			if (trigger!=null) {
				trigger.shutdown();
			}
			
			trigger = new PeriodicReloadingTrigger(controller, null, 60000, TimeUnit.MILLISECONDS);
			trigger.start();
			logger.info("Scheduled reloading trigger");
		}
	}
	
	private static void createMultiFileConfig (String masterfile) throws ConfigurationException
	{
		Parameters params = new Parameters();
		logger.info("Start creating multi file config from file "+masterfile);
		ReloadingCombinedConfigurationBuilder b = new ReloadingCombinedConfigurationBuilder();
		b.configure(params.fileBased().setFileName(masterfile)
				.setReloadingRefreshDelay(10000L)
				.setReloadingDetectorFactory(new ReloadingDetectorFactoryImpl())
				.setEncoding("UTF-8")
				.setListDelimiterHandler(new DefaultListDelimiterHandler((char) 0))
				);	
		builder2=b;
		conf = builder2.getConfiguration();
		installReloadTrigger(b.getReloadingController());

		logger.info("Created multi file config from file "+masterfile);
	}
	
	private synchronized void init()  
	{
		try {
			if (configPattern!=null) {
				createMultiFileConfig(configPattern);
			}
			else {
				logger.warn("create empty config, as config files are missing");
				conf = new BaseHierarchicalConfiguration();
			}
		}
		catch (ConfigurationException cex) {
			logger.error("Configuration failure", cex);
		}	
	}
	
	static private Object m_Sync = new Object();
	
/*Diese Methode gibt die aktuelle Konfiguration zurück. Hierfür verwendet sie die BaseHierarchicalConfiguration-Klasse von Apache*/
	
	public static BaseHierarchicalConfiguration getConfiguration() {
		if (reloadRequired!=RELOAD_NOT_REQUIRED) {
			synchronized(m_Sync) {
				if (reloadRequired!=RELOAD_NOT_REQUIRED) {
					try {
						if (builder2 instanceof ReloadingCombinedConfigurationBuilder /*&& reloadRequired==RELOAD_FULLY_REQUIRED*/) {
							ConfigurationHolder3.createMultiFileConfig(configPattern);
							logger.info("Rescheduled reloading trigger");
						} 
						else {
							conf.clear();
							conf = builder2.getConfiguration();
							if (builder2 instanceof ReloadingCombinedConfigurationBuilder) {
								installReloadTrigger(((ReloadingCombinedConfigurationBuilder)builder2).getReloadingController());
							}
						}
						reloadRequired=RELOAD_NOT_REQUIRED;
					} 
					catch (ConfigurationException e) {
						logger.error("Reloading Configuration failed. Please fix whatever you did",e);
					}
				}
			}
		}

		return conf;
	}
		
	
	private static class ReloadingDetectorFactoryImpl implements ReloadingDetectorFactory {
		
		@Override
		public ReloadingDetector createReloadingDetector(FileHandler arg0, FileBasedBuilderParametersImpl arg1)
				throws ConfigurationException {
			// TODO Auto-generated method stub
			logger.debug("Building ReloadingDetector");
			ReloadingDetectorImpl impl = new ReloadingDetectorImpl(arg0.getFile());
			return impl;
		}

	}
	
	private static class ReloadingDetectorImpl implements ReloadingDetector {
		
		private long lastModified=-1L;
		private File f = null;
		
		private ReloadingDetectorImpl(File f) {
			this.f=f;
			lastModified=f.lastModified();
			logger.debug("Added last modified reloading detector to file " +f.getName());
		}
		
		@Override
		public void reloadingPerformed() {
			logger.debug("Reloading configuration has been performed");
		}

		@Override
		public boolean isReloadingRequired() {
			if (f.lastModified()>lastModified) {
				lastModified=f.lastModified();
				if (f.getAbsolutePath().equals(configPattern)) {
					ConfigurationHolder3.setReloadingRequired(RELOAD_FULLY_REQUIRED);
					logger.debug("Complete reloading configuration is required ("+f.getName()+" changed)");
				}
				else {
					ConfigurationHolder3.setReloadingRequired(RELOAD_REQUIRED);
					logger.debug("Reloading configuration is required ("+f.getName()+" changed)");
				}
				return true;
			}
			
			return false;
		}

	}
	
	public static void shutdown() {
		if (trigger!=null)
			trigger.shutdown();
		
		if (conf!=null)
			conf=null;
		
	}

}
