package com.soze.hashtag.service.analysis;

/**
 * Interface which objects who calculate statistics about hashtags implement.
 * 
 * @author kamil jurek
 *
 */
public interface HashtagAnalysis {

	public void analyse();
	public AnalysisResults getResults();
	
}
