package de.fomwebtech.exception;

public class ConstraintViolationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3853137308545906119L;

	public ConstraintViolationException() {
		super();
	}
	
	public ConstraintViolationException(String s) {
		super(s);
	}
	
	public ConstraintViolationException(String s,Throwable t) {
		super(s,t);
	}
}
