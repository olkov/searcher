package com.searcher.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.searcher.dto.SearchMatch;

public class StringMatcher {

	public Map<String, List<SearchMatch>> findMatches(String phrase, Set<String> keys) {
		Map<String, List<SearchMatch>> matches = new HashMap<>();
		if (!phrase.isBlank()) {
			keys.forEach(key -> {
				List<SearchMatch> charOffsets = new Stack<>();
				for (int charOffset = -1; (charOffset = phrase.indexOf(key, charOffset + 1)) != -1; ) {
					charOffsets.add(SearchMatch.builder().charOffset(charOffset + 1).build());
				}
				matches.put(key, charOffsets);
			});
		}
		return matches;
	}
}
