package de.fomwebtech.database.connectors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.fomwebtech.configuration.ConfigurationHolder3;

/*Die Klasse HikariDataSourceManager wird benötigt Connections Pools, also Verbindungen zur Datenbank zu verwalten. 
 * Sie enthält Methoden, Datenbankverbindungen herzustellen und diese wieder zu schließen. Hierfür werden externe Parameter übergeben die eine HikariDataSource-Instanz erstellen und dann in einer HAsmap gespeichert werden. 
 * (wird von der Library HikariCP bereitgestellt - wird importiert).*/

public class HikariDataSourceManager {
	
	private static HashMap<String, HikariDataSource> dsMap = new HashMap<String,HikariDataSource>();
	private static boolean isInitialized = false;
	private static Logger logger = LogManager.getLogger(HikariDataSourceManager.class);
	
	
	static Connection getConnection(String pool) throws SQLException {
		return dsMap.get(pool).getConnection();
	}
	
	static void closeConnection(Connection con) throws SQLException {
        con.close();
	}
	
	public static void initializePools() {
		
		if (!isInitialized) {
			logger.info("Initializing connection pools");
			HierarchicalConfiguration <?> conf =  ConfigurationHolder3.getConfiguration().configurationAt("jdbcDatasources");
			ImmutableNode n=conf.getNodeModel().getInMemoryRepresentation();
			for (ImmutableNode a: n.getChildren()) {
				HikariConfig jdbcConfig = new HikariConfig();
				final String item = a.getNodeName();
				try {
					
					logger.info("Trying to initialize DataSource with parameter from " + item);
					HierarchicalConfiguration <?> dsConfig = conf.configurationAt(item);
					jdbcConfig.setPoolName(dsConfig.getString("poolName"));
			        jdbcConfig.setMaximumPoolSize(dsConfig.getInt("maximumPoolSize",10));
			        jdbcConfig.setMinimumIdle(dsConfig.getInt("minimumIdle",2));
			        jdbcConfig.setJdbcUrl(dsConfig.getString("jdbcUrl"));
			        jdbcConfig.setUsername(dsConfig.getString("username"));
			        jdbcConfig.setConnectionTestQuery(dsConfig.getString("validationQuery"));
			        jdbcConfig.setValidationTimeout(dsConfig.getLong("validationTimeoutMillis",10000L));
			        jdbcConfig.setConnectionTimeout(dsConfig.getLong("connectionTimeoutMillis",10000L));
			        jdbcConfig.setConnectionInitSql(dsConfig.getString("connectionInitSql"));
			        jdbcConfig.setLeakDetectionThreshold(dsConfig.getLong("leakDetectionThresholdMillis",15000L));
			        jdbcConfig.setPassword(dsConfig.getString("password"));
			        jdbcConfig.setAutoCommit(dsConfig.getBoolean("autoCommit",true));
			        jdbcConfig.setDriverClassName(dsConfig.getString("driverClassName"));
			        jdbcConfig.addDataSourceProperty("cachePrepStmts", dsConfig.getBoolean("cachePrepStmts",true));
			        jdbcConfig.addDataSourceProperty("prepStmtCacheSize", dsConfig.getInt("prepStmtCacheSize",0));
			        jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", dsConfig.getInt("prepStmtCacheSqlLimit",0));
			        jdbcConfig.addDataSourceProperty("useServerPrepStmts", dsConfig.getBoolean("useServerPrepStmts",true));
			
			        HikariDataSource ds = new HikariDataSource(jdbcConfig);
					dsMap.put(dsConfig.getString("poolName"), ds);
				
				}
				catch (Exception e) {
					logger.error("Could not initilize pool for " + item,e);
				}

			}
				
			logger.info("Done initializing database pools");
			isInitialized=true;
		}
		else {
			logger.warn("You can only initialize connection pools once");
		}
		
		
	}
	
	public static void shutDownPools() {
		if (isInitialized) {
			for (Iterator<String> it=dsMap.keySet().iterator();it.hasNext();) {
				String poolName = it.next();
				HikariDataSource ds = dsMap.get(poolName);
				logger.info("Trying to shutdown connection pool with name: " + poolName);
				ds.close();

			}
		}
		isInitialized=false;
	}


}
