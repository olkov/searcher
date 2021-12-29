package com.searcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMatch {

	private Integer lineOffset;
	private Integer charOffset;

	public String toString() {
		return "[lineOffset=" + lineOffset + ", charOffset=" + charOffset + "]";
	}
}
