package com.soze.hashtag.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.hashtag.service.analysis.AnalysisFactory;
import com.soze.hashtag.service.analysis.AnalysisFactory.AnalysisType;
import com.soze.hashtag.service.analysis.AnalysisResults;
import com.soze.hashtag.service.analysis.DummyHashtagAnalysis;
import com.soze.hashtag.service.analysis.HashtagAnalysis;
import com.soze.hashtag.service.analysis.HashtagScore;
import com.soze.hashtag.service.analysis.SimpleHashtagAnalysis;
import com.soze.hashtag.service.analysis.StatisticalHashtagAnalysis;
import com.soze.kluch.service.KluchService;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class HashtagAnalysisServiceTest extends TestWithMockUsers {
	
	@Autowired
	private AnalysisFactory factory;
	
	@Autowired
	private KluchService service;

	@Test
	public void testFactory() {
		
		HashtagAnalysis analysis = factory.getAnalysis(AnalysisType.NONE);
		assertThat(analysis.getClass(), equalTo(DummyHashtagAnalysis.class));
		
		analysis = factory.getAnalysis(AnalysisType.SIMPLE);
		assertThat(analysis.getClass(), equalTo(SimpleHashtagAnalysis.class));
		
		analysis = factory.getAnalysis(AnalysisType.STATISTICAL);
		assertThat(analysis.getClass(), equalTo(StatisticalHashtagAnalysis.class));
		
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testFactoryNullParameter() {
		
		factory.getAnalysis(null);
		
	}
	
	@Test
	public void testDummyAnalysis() {
		
		HashtagAnalysis analysis = factory.getAnalysis(AnalysisType.NONE);
		analysis.analyse();
		AnalysisResults results = analysis.getResults();
		assertThat(results, notNullValue());
		assertThat(results.getHashtagScores(), notNullValue());
		assertTrue(results.getHashtagScores().isEmpty());
		
	}
	
	@Test
	public void testSimpleAnalysisNoKluchs() {
		
		HashtagAnalysis analysis = factory.getAnalysis(AnalysisType.SIMPLE);
		analysis.analyse();
		AnalysisResults results = analysis.getResults();
		assertThat(results, notNullValue());
		assertThat(results.getHashtagScores(), notNullValue());
		assertTrue(results.getHashtagScores().isEmpty());
		
	}
	
	@Test
	public void testStatisticalAnalysisNoKluchs() {
		
		HashtagAnalysis analysis = factory.getAnalysis(AnalysisType.STATISTICAL);
		analysis.analyse();
		AnalysisResults results = analysis.getResults();
		assertThat(results, notNullValue());
		assertThat(results.getHashtagScores(), notNullValue());
		assertTrue(results.getHashtagScores().isEmpty());
		
	}
	
	@Test
	public void testSimpleAnalysis() {
		
		HashtagAnalysis analysis = factory.getAnalysis(AnalysisType.STATISTICAL);
		analysis.analyse();
		AnalysisResults results = analysis.getResults();
		assertThat(results, notNullValue());
		assertThat(results.getHashtagScores(), notNullValue());
		assertTrue(results.getHashtagScores().isEmpty());
		
		postKluch("#one", "test");
		postKluch("#one also text", "test");
		
		analysis.analyse();
		results = analysis.getResults();
		assertThat(results, notNullValue());
		List<HashtagScore> scores = results.getHashtagScores();
		assertThat(scores, notNullValue());
		assertFalse(scores.isEmpty());
		assertThat(scores.size(), equalTo(1));
		assertThat(scores.get(0).getHashtag(), equalTo("one"));
		assertThat(scores.get(0).getScore(), equalTo(1.8f));
		
	}
	
	private void postKluch(String text, String username) {
		mockUser(username);
		service.post(username, text);
	}
	
	

}
