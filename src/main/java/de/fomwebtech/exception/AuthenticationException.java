package de.fomwebtech.exception;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AuthenticationException() {
		super();
	}
	
	public AuthenticationException(String info) {
		super(info);
	}
	
	public AuthenticationException (String info, Throwable t) {
		super(info,t);
	}

}
