package com.soze.hashtag.service.analysis;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;

public class SimpleHashtagAnalysis implements HashtagAnalysis {

	private static final Logger log = LoggerFactory.getLogger(SimpleHashtagAnalysis.class);
	private final KluchDao kluchDao;
	/** How many days to analyse. */
	private final int daysBack;
	private final int maximumTrendingHashtags;
	private AnalysisResults results;
	
	
	@Autowired
	public SimpleHashtagAnalysis(KluchDao kluchDao, int daysBack, int maximumTrendingHashtags) {
		this.kluchDao = kluchDao;
		this.daysBack = daysBack;
		this.maximumTrendingHashtags = maximumTrendingHashtags;
	}

	@Override
	public void analyse() {
		log.info("Running simple analysis.");
		long startingTime = System.nanoTime();
		Instant now = Instant.now();
		Instant twoDaysAgo = now.minus(daysBack, ChronoUnit.DAYS);
		List<Kluch> kluchs = kluchDao.findAllAfterTimestamp(new Timestamp(twoDaysAgo.toEpochMilli()));
		log.info("[{}] kluchs posted since [{}] days ago. ", kluchs.size(), daysBack);
		computeResults(kluchs);
		log.info("Analysis took [{}] seconds", ((System.nanoTime() - startingTime) / 1e9));
	}
	
	private void computeResults(List<Kluch> kluchs) {
		Map<String, Integer> hashtagCounts = new HashMap<>();
		for(Kluch kluch: kluchs) {
			computeKluch(kluch, hashtagCounts);
		}
		assembleResult(hashtagCounts);
	}
	
	private void computeKluch(Kluch kluch, Map<String, Integer> hashtagCounts) {
		Set<String> hashtags = kluch.getHashtags();
		for(String hashtag: hashtags) {
			Integer currentCount = hashtagCounts.get(hashtag);
			hashtagCounts.put(hashtag, currentCount == null ? 1 : ++currentCount);
		}
	}
	
	private void assembleResult(Map<String, Integer> hashtagCounts){
		Set<Entry<String, Integer>> entrySet = hashtagCounts.entrySet();
		List<Entry<String, Integer>> entryList = new ArrayList<>(entrySet);
		// sort in descending order
		Collections.sort(entryList, (e1, e2) -> e2.getValue() - e1.getValue());
		List<Entry<String, Integer>> topResults = entryList.stream()
				.limit(maximumTrendingHashtags)
				.collect(Collectors.toList());
		convertEntriesToCounts(topResults);
	}
	
	private void convertEntriesToCounts(List<Entry<String, Integer>> entries) {
		List<HashtagCount> hashtagCounts = new ArrayList<>();
		for(Entry<String, Integer> entry: entries) {
			hashtagCounts.add(new HashtagCount(entry.getKey(), entry.getValue()));
		}
		results = new AnalysisResults(hashtagCounts);
		log.info("Analysis finished, results are: {}", results);
	}

	@Override
	public AnalysisResults getResults() {
		return results;
	}

}
