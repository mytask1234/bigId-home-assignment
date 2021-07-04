package com.bigid.exception;

public class CustomException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4223095394191338480L;

	public CustomException() {

	}

	public CustomException(String message) {
		super(message);
	}

	public CustomException(Throwable cause) {
		super(cause);
	}

	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
