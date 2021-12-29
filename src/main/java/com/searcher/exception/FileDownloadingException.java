package com.searcher.exception;

public class FileDownloadingException extends RuntimeException {

	public FileDownloadingException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
