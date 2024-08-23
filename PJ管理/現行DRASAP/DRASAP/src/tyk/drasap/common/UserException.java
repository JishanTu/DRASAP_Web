package tyk.drasap.common;

public class UserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3855991178713229852L;

	/**
	 * 
	 */
	public UserException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UserException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UserException(Throwable cause) {
		super(cause);
	}

}
