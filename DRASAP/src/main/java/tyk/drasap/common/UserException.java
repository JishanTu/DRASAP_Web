package tyk.drasap.common;

public class UserException extends Exception {
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
