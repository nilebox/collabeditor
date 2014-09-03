package ru.nilebox.collabedit.transform;

/**
 *
 * @author nile
 */
public class TransformException extends Exception {
	private static final long serialVersionUID = -7970909528709224668L;

	public TransformException() {
	}

	public TransformException(String message) {
		super(message);
	}

	public TransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformException(Throwable cause) {
		super(cause);
	}

	public TransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
