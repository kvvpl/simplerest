package com.markocki.converter;

public class RecordsUploaderFileParseException extends Exception {
	public RecordsUploaderFileParseException(String msg) {
		super(msg);
	}

	public RecordsUploaderFileParseException(String msg, Exception exc) {
		super(msg,exc);
	}
}
