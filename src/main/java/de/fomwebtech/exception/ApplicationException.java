package de.fomwebtech.exception;

/*Diese Klasse enthält eigene definierte Exceptions. */
public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3853137308545906119L;

	public ApplicationException() {
		super();
	}
	
	public ApplicationException(String s) {
		super(s);
	}
	
	public ApplicationException(String s,Throwable t) {
		super(s,t);
	}
}
