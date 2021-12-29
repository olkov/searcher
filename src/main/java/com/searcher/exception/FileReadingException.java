package com.searcher.exception;

public class FileReadingException extends RuntimeException {

	public FileReadingException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
