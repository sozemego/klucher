package com.soze.hashtag.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.hashtag.service.analysis.AnalysisResults;
import com.soze.hashtag.service.analysis.HashtagAnalysis;

/**
 * A service which analyses trends among hashtags in user posts.
 * @author kamil jurek
 *
 */
@Service
public class HashtagAnalysisService {

	private HashtagAnalysis currentAnalysis;
	
	public void setAnalysisStrategy(HashtagAnalysis analysis) throws NullOrEmptyException {
		this.currentAnalysis = validate(analysis);
	}
	
	@Scheduled(initialDelayString = "10000000", fixedDelayString = "${hashtag.analysis.interval}")
	public void analyse() {
		currentAnalysis.analyse();
	}
	
	public AnalysisResults getResults() {
		return currentAnalysis.getResults();
	}
	
	private HashtagAnalysis validate(HashtagAnalysis analysis) {
		if(analysis != null) {
			return analysis;
		}
		throw new NullOrEmptyException("Analysis");
	}
	
}
