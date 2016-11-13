package com.soze.hashtag.service.analysis;

import java.util.ArrayList;

public class DummyHashtagAnalysis implements HashtagAnalysis {

	@Override
	public void analyse() {
		
	}

	@Override
	public AnalysisResults getResults() {
		return new AnalysisResults(new ArrayList<>());
	}

}
