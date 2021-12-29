package com.searcher.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.searcher.exception.FileDownloadingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileClient {

	private static final Logger log = LogManager.getLogger(FileClient.class);

	private final HttpClient client = HttpClient.newHttpClient();

	public File download(String url) {
		log.info("Downloading file: {}", url);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		InputStream in = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
				.thenApply(HttpResponse::body).join();
		String fileName = determineFileName(url);
		File file = new File(fileName);
		try (FileOutputStream out = new FileOutputStream(file)) {
			in.transferTo(out);
			log.info("File successfully downloaded to: {}", file.getAbsoluteFile());
			return file;
		} catch (Exception ex) {
			log.info("Unable to download file: {}", fileName);
			throw new FileDownloadingException("Unable to download file: " + fileName, ex);
		}
	}

	private String determineFileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}
}
