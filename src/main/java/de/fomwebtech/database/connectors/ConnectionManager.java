package de.fomwebtech.database.connectors;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*Methode zum Verbinden mit der Datenbank über Connection Pool aus der Klasse Hikari DataSourceManager*/
public class ConnectionManager {

		private Logger logger = LogManager.getLogger(this.getClass());	    
	    private String pool;


        public ConnectionManager (String pool) throws NamingException {
        	this.pool=pool;
        	logger.debug("Trying to get connetion from connection pool " + pool);
        }
        
        public Connection getConnection() {
            try {
            	return HikariDataSourceManager.getConnection(pool);
            }
            catch (SQLException sqle) {
                    logger.error("Problem getting DB Connection from pool " + pool, sqle);
                    return null;
            }
        }
        
        public void closeConnection(Connection con) {
            try {
                    if (con != null)
                    	HikariDataSourceManager.closeConnection(con);
            }
            catch (SQLException sqle) {
                    logger.error("Problem closing DB Connection from pool " + pool, sqle);
            }
    }
        

}
