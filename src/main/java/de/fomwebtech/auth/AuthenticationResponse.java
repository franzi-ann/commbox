package de.fomwebtech.auth;

import org.json.JSONObject;

public class AuthenticationResponse {
	
	private final int httpStatus;
	private final JSONObject json;
	
	public AuthenticationResponse(int httpStatus,JSONObject json) {
		this.httpStatus = httpStatus;
		this.json = json;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public JSONObject getJson() {
		return json;
	}

}
