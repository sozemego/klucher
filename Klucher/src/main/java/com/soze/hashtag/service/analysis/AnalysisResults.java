package com.soze.hashtag.service.analysis;

import java.util.List;

public class AnalysisResults {

	private final List<HashtagCount> hashtagCounts;
	
	public AnalysisResults(List<HashtagCount> hashtagCounts) {
		this.hashtagCounts = hashtagCounts;
	}

	public List<HashtagCount> getHashtagCounts() {
		return hashtagCounts;
	}
	
	@Override
	public String toString() {
		String toReturn = "This analysis includes " + hashtagCounts.size() + " hashtags. "; 
		for (HashtagCount count : hashtagCounts) {
			toReturn += "[" + count.getHashtag() + " - " + count.getCount() + "]\n";
		}
		return toReturn;
	}
	
}
