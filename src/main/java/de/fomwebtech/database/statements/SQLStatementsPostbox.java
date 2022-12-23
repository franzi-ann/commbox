package de.fomwebtech.database.statements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.fomwebtech.database.connectors.ConnectionManager;
import de.fomwebtech.email.FileMetaData;
import de.fomwebtech.exception.ApplicationException;

public class SQLStatementsPostbox {

	private final ConnectionManager manager;
	private final Logger logger = LogManager.getLogger(this.getClass());

	public SQLStatementsPostbox() throws ApplicationException {
		try {
			manager = new ConnectionManager("postbox");
			
		} catch (Exception e) {
			logger.error("Could not get database connection to aax DB", e);
			throw new ApplicationException("Fehler bei DB Verbindung");
		}
	}

	public void removeEntry(FileMetaData metaData) {
		if (manager != null) {
			Connection con = manager.getConnection();
			try {
				PreparedStatement ps = con.prepareStatement("delete from postbox_metadata where email= ? and filename = ?");
				ps.setString(1, metaData.getCustomerEmail());
				ps.setString(2, metaData.getFilename());
				ps.execute();
				ps.close();
				if (!con.getAutoCommit())
					con.commit();
			}
			
			catch (Exception e) {
				logger.error("Something went wrong while removing metadata", e);

			} 
			finally {
				manager.closeConnection(con);
			}
		}
	}
	
	public void addEntry(FileMetaData metaData) {

			
		if (manager != null) {
			Connection con = manager.getConnection();
			try {

				PreparedStatement ps = con.prepareStatement("insert into postbox_metadata (email, filename,subject,preview) values (?,?,?,?)");
				ps.setString(1, metaData.getCustomerEmail());
				ps.setString(2, metaData.getFilename());
				ps.setString(3, metaData.getSubject());
				ps.setString(4, metaData.getPreviewContent());
				ps.execute();
				ps.close();
				if (!con.getAutoCommit())
					con.commit();

			}
			
			catch (Exception e) {
				logger.error("Something went wrong while adding metadata", e);

			} 
			finally {
				manager.closeConnection(con);
			}
		}
	}
	
	public JSONArray getEntries(String email) {

		JSONArray jar = new JSONArray();
		
		if (manager != null) {
			Connection con = manager.getConnection();
			try {

				PreparedStatement ps = con.prepareStatement("select * from postbox_metadata where email = ? order by insertation_dt desc");
				ps.setString(1, email.trim());
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					JSONObject json = new JSONObject();
					json.put("email", rs.getString("email"));
					json.put("filename", rs.getString("filename"));
					json.put("subject", rs.getObject("subject")!=null?rs.getString("subject"):JSONObject.NULL);
					json.put("preview", rs.getObject("preview")!=null?rs.getString("preview"):JSONObject.NULL);
					json.put("is_read", rs.getBoolean("is_read"));
					json.put("insertation_dt", rs.getTimestamp("insertation_dt").toInstant().toEpochMilli());
					if (rs.getObject("document_dt")!=null)
						json.put("document_dt", rs.getTimestamp("document_dt").toInstant().toEpochMilli());
					jar.put(json);
				}
				rs.close();
				ps.close();

			}
			
			catch (Exception e) {
				logger.error("Something went wrong while gathering metadata", e);

			} 
			finally {
				manager.closeConnection(con);
			}
		}
		return jar;
	}

	public boolean validateGetFile(String email,String filename) {

		boolean isValid = false;
		
		if (manager != null) {
			Connection con = manager.getConnection();
			try {

				PreparedStatement ps = con.prepareStatement("select 1 from postbox_metadata where email = ? and filename = ?");

				ps.setString(1, email);
				ps.setString(2, filename);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) 
					isValid = true;
				
				rs.close();
				ps.close();

			}
			
			catch (Exception e) {
				logger.error("Something went while validating getFile", e);

			} 
			finally {
				manager.closeConnection(con);
			}
		}
		return isValid;
	}

}