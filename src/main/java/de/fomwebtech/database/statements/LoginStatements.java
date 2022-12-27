package de.fomwebtech.database.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.fomwebtech.database.connectors.ConnectionManager;
import de.fomwebtech.tools.HashGenerator;
import de.fomwebtech.user.User;

public class LoginStatements {
	
	private Logger logger = LogManager.getLogger(this.getClass());
	private ConnectionManager cm;

	//Überprüfen ob Connection zur Datenbank möglich und baut diese dann auf
	public LoginStatements() {
		try {
			cm = new ConnectionManager("postbox");
		} 
		catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.error("This should never happen if your jdbc-datasources configuration is alright",e);
		}
	}
	
/*Methode zum Validieren des Logins: Es wird eine Datenbankverbindung aufgebaut und die übergebenen Daten E-Mail werden im select Statement abgefragt.
Es wird überprüft ob der SHA1 passwordhash mit dem passwordhash aus der Datenbank übereinstimmt. Stimmt dieser überein wird ein Userobjekt generiert und zurückgegeben.*/
	
	public User validateLoginData(String email,String password) {
		
		Connection con = null;
		User user = null;
		try {
			con = cm.getConnection();
			PreparedStatement ps = con.prepareStatement("select us.*"
					+ " from users us"
					+ " where us.email = ?");
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (HashGenerator.createSHA1PasswordHash(password).matches(rs.getString("password")) ) {
					user = new User(email);
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setUsername(rs.getString("username"));
				}
			}
			rs.close();
			ps.close();
			
		} 
		catch (Exception e) {
			// we catch any exception
			logger.error("",e);
		} 
		finally {
			if (cm!=null&&con!=null)
				cm.closeConnection(con);
		}
		
		return user;
	}
	
	
}
