package com.searcher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.searcher.dto.SearchMatch;

public class SearchMatchersAggregator {

	public Map<String, List<SearchMatch>> aggregate(List<Map<String, List<SearchMatch>>> searchMatches) {
		return searchMatches.stream()
				.flatMap(list -> list.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (list1, list2) -> {
					List<SearchMatch> list = new ArrayList<>(list1);
					list.addAll(list2);
					return list;
				}));
	}
}
