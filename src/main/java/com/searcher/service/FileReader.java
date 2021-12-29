package com.searcher.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.searcher.exception.FileReadingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileReader {

	private static final Logger log = LogManager.getLogger(FileReader.class);
	public static final Integer BATCH_SIZE = 1_000;

	public List<List<String>> getLinesBatches(File file) {
		List<List<String>> linesBatches = new ArrayList<>();
		long linesNumber = countLines(file);
		int offset = 0;
		while (linesNumber > offset) {
			List<String> lines = readLines(file, offset, BATCH_SIZE);
			linesBatches.add(lines);
			offset += BATCH_SIZE;
		}
		return linesBatches;
	}

	private Long countLines(File file) {
		try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
			return stream.count();
		} catch (IOException ex) {
			log.error("Unable to read file: {}", file.getAbsoluteFile(), ex);
			throw new FileReadingException("Unable to read file: " + file.getAbsoluteFile(), ex);
		}
	}

	private List<String> readLines(File file, Integer offset, Integer size) {
		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			return reader.lines()
					.skip(offset)
					.limit(size)
					.collect(Collectors.toList());
		} catch (IOException ex) {
			log.error("Unable to read file: {}", file.getAbsoluteFile(), ex);
			throw new FileReadingException("Unable to read file: " + file.getAbsoluteFile(), ex);
		}
	}
}
