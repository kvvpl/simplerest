package com.markocki.converter;

public class RecordsUploaderInternalException extends Exception {
	public RecordsUploaderInternalException(String message) {
		super(message);
	}
	public RecordsUploaderInternalException(String message, Exception exc) {
		super(message,exc);
	}
}
