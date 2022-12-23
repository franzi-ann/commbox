package de.fomwebtech.exception;

public class SQLRollbackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3853137308545906119L;

	public SQLRollbackException() {
		super();
	}
	
	public SQLRollbackException(String s) {
		super(s);
	}
}
