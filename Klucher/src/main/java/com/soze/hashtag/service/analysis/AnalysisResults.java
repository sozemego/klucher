package com.soze.hashtag.service.analysis;

import java.util.List;

public class AnalysisResults {

	private final List<HashtagScore> hashtagScores;
	
	public AnalysisResults(List<HashtagScore> hashtagScores) {
		this.hashtagScores = hashtagScores;
	}

	public List<HashtagScore> getHashtagScores() {
		return hashtagScores;
	}
	
	@Override
	public String toString() {
		String toReturn = "This analysis includes " + hashtagScores.size() + " hashtags. "; 
		for (HashtagScore count : hashtagScores) {
			toReturn += "[" + count.getHashtag() + " - " + count.getScore() + "]\n";
		}
		return toReturn;
	}
	
}
