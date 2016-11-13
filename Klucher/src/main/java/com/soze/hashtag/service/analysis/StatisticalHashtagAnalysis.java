package com.soze.hashtag.service.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticalHashtagAnalysis implements HashtagAnalysis {

	private static final Logger log = LoggerFactory.getLogger(StatisticalHashtagAnalysis.class);
	
	@Override
	public void analyse() {
		log.info("Running statistical analysis.");
		
	}

	@Override
	public AnalysisResults getResults() {
		// TODO Auto-generated method stub
		return null;
	}

}
