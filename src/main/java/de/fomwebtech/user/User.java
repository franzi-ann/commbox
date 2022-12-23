package de.fomwebtech.user;

import org.json.JSONObject;

public class User {
	
	private final String email;
	private String first_name = "";
	private String last_name = "";
	private String username = "";
	
	
	public User (String email) {
		this.email = email;
	}
	
	public User (String username, String email, String first_name, String last_name) {
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
	}
	

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("first_name", getFirstName());
		json.put("last_name", getLastName());
		json.put("email", getEmail());
		json.put("username", getUsername());
		
		return json;
	}
	
}



