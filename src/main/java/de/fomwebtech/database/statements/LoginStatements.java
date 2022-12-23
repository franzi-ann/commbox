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

	public LoginStatements() {
		try {
			cm = new ConnectionManager("postbox");
		} 
		catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.error("This should never happen if your jdbc-datasources configuration is alright",e);
		}
	}
	
	
	public User validateLoginData(String email,String password,String trainer_id) {
		
		Connection con = null;
		User user = null;
		try {
			con = cm.getConnection();
			long coach = Long.parseLong(trainer_id);
			PreparedStatement ps = con.prepareStatement("select us.*"
					+ " from users us"
					+ " where us.email = ?");
			ps.setString(1, email);
			ps.setLong(2, coach);
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
