package de.fomwebtech.auth;

import org.json.JSONObject;

/*Diese Klasse dient der Rückgabe an den Athentication Filter. Hintergrund ist, dass Appclients eher ein Json Objekt auswerten als einen Cookie. 
 * Diese brauchen oftmals einen http Status und ein Json. Hiermit wird die Grundlage für eine Implementierung in der App gelegt.*/
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
