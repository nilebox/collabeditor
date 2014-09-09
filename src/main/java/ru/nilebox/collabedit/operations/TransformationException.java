package ru.nilebox.collabedit.operations;

/**
 * Exception to be thrown when transformation is failed
 * @author nile
 */
public class TransformationException extends Exception {
	private static final long serialVersionUID = -7970909528709224668L;

	public TransformationException() {
	}

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformationException(Throwable cause) {
		super(cause);
	}

	public TransformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
