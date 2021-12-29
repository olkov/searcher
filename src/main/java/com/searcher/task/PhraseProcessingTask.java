package com.searcher.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import com.searcher.dto.SearchMatch;
import com.searcher.service.FileReader;
import com.searcher.service.StringMatcher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PhraseProcessingTask implements Callable<List<Map<String, List<SearchMatch>>>> {

	private final CountDownLatch countDownLatch;
	private final StringMatcher stringMatcher;
	private final List<String> lines;
	private final Set<String> keys;
	private final Integer index;

	@Override
	public List<Map<String, List<SearchMatch>>> call() {
		List<Map<String, List<SearchMatch>>> matches = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			Integer lineOffset = (index * FileReader.BATCH_SIZE) + (i + 1);
			Map<String, List<SearchMatch>> matchesPerLine = stringMatcher.findMatches(lines.get(i), keys);
			matchesPerLine.forEach((k, v) -> v.forEach(match -> match.setLineOffset(lineOffset)));
			matches.add(matchesPerLine);
		}
		countDownLatch.countDown();
		return matches;
	}
}
