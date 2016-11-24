package com.soze.hashtag.service.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.kluch.dao.KluchDao;

@Service
public class AnalysisFactory {

	/** Default value for how many days in the past should SimpleHashtagAnalysis look into */
	private static final int DEFAULT_NUMBER_OF_DAYS_TO_ANALYSE = 2;
	private static final int DEFAULT_MAXIMUM_TRENDING_HASHTAGS = 10;
	private static final HashtagAnalysis DUMMY_ANALYSIS = new DummyHashtagAnalysis();
	private final KluchDao kluchDao;
	/** This analysis is a field because its calculations 
	 * are supposed to be carried out over a course of app use. 
	 * It stores results and each subsequent analysis refines the results. */
	private final StatisticalHashtagAnalysis statisticalAnalysis;
	
	@Autowired
	public AnalysisFactory(KluchDao kluchDao) {
		this.kluchDao = kluchDao;
		this.statisticalAnalysis = new StatisticalHashtagAnalysis(kluchDao, DEFAULT_NUMBER_OF_DAYS_TO_ANALYSE, DEFAULT_MAXIMUM_TRENDING_HASHTAGS);
	}
	
	public HashtagAnalysis getAnalysis(AnalysisType type) throws NullOrEmptyException {
		if(type == AnalysisType.NONE) {
			return DUMMY_ANALYSIS;
		}
		if(type == AnalysisType.SIMPLE) {
			return new SimpleHashtagAnalysis(kluchDao, DEFAULT_NUMBER_OF_DAYS_TO_ANALYSE, DEFAULT_MAXIMUM_TRENDING_HASHTAGS);
		}
		if(type == AnalysisType.STATISTICAL) {
			return statisticalAnalysis;
		}
		throw new NullOrEmptyException("Analysis type");
	}
	
	public enum AnalysisType {
		NONE, SIMPLE, STATISTICAL;
	}
}
