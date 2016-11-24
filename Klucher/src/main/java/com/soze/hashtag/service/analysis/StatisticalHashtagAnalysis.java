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

public class StatisticalHashtagAnalysis implements HashtagAnalysis {

	private static final Logger log = LoggerFactory.getLogger(StatisticalHashtagAnalysis.class);
	private final KluchDao kluchDao;
	/** How many days to analyse. */
	private final int daysBack;
	private final int maximumTrendingHashtags;
	/** Base score for each hashtag is 1. If the same user keeps posting the same hashtag
	 * the score is multiplied by this coefficient for each same hashtag posted by the user (above 1). */
	private final float hashtagScoreByUserDecayCoefficient = 0.8f;
	private AnalysisResults results;
	
	
	@Autowired
	public StatisticalHashtagAnalysis(KluchDao kluchDao, int daysBack, int maximumTrendingHashtags) {
		this.kluchDao = kluchDao;
		this.daysBack = daysBack;
		this.maximumTrendingHashtags = maximumTrendingHashtags;
	}

	@Override
	public void analyse() {
		log.info("Running statistical analysis.");
		long startingTime = System.nanoTime();
		Instant now = Instant.now();
		Instant twoDaysAgo = now.minus(daysBack, ChronoUnit.DAYS);
		List<Kluch> kluchs = kluchDao.findAllAfterTimestamp(new Timestamp(twoDaysAgo.toEpochMilli()));
		log.info("[{}] kluchs with hashtags posted since [{}] days ago. ", kluchs.size(), daysBack);
		computeResults(kluchs);
		log.info("Analysis took [{}] seconds", ((System.nanoTime() - startingTime) / 1e9));
	}
	
	private void computeResults(List<Kluch> kluchs) {
		Map<String, Float> hashtagScores = new HashMap<>();
		Map<String, List<String>> userHashtags = new HashMap<>();
		for(Kluch kluch: kluchs) {
			computeKluch(kluch, hashtagScores, userHashtags);
		}
		assembleResult(hashtagScores);
	}
	
	private void computeKluch(Kluch kluch, Map<String, Float> hashtagScores, Map<String, List<String>> userHashtags) {
		Set<String> hashtags = kluch.getHashtags();
		for(String hashtag: hashtags) {
			Float score = hashtagScores.get(hashtag);
			if(score == null) {
				score = 0f;
				hashtagScores.put(hashtag, score);
			}
			String username = kluch.getAuthor().getUsername();
			List<String> hashtagsByUser = userHashtags.get(username);
			if(hashtagsByUser == null) {
				hashtagsByUser = new ArrayList<>();
				userHashtags.put(username, hashtagsByUser);
			}
			int postedByThisUserSoFar = getPostedByThisUserSoFar(hashtag, hashtagsByUser);
			float addedScore = 1f;
			if(postedByThisUserSoFar != 0) {
				addedScore *= Math.pow(hashtagScoreByUserDecayCoefficient, postedByThisUserSoFar);
			}
			score += addedScore;
			hashtagScores.put(hashtag, score);
			hashtagsByUser.add(hashtag);
		}
	}
	
	private int getPostedByThisUserSoFar(String hashtag, List<String> hashtags) {
		return (int) hashtags.stream().filter(h -> h.equals(hashtag)).count();
	}
	
	private void assembleResult(Map<String, Float> hashtagCounts){
		Set<Entry<String, Float>> entrySet = hashtagCounts.entrySet();
		List<Entry<String, Float>> entryList = new ArrayList<>(entrySet);
		// sort in descending order
		Collections.sort(entryList, (e1, e2) -> {
			if(e1.getValue() > e2.getValue()) return -1;
			if(e1.getValue() < e2.getValue()) return 1;
			return 0;
		});
		List<Entry<String, Float>> topResults = entryList.stream()
				.limit(maximumTrendingHashtags)
				.collect(Collectors.toList());
		convertEntriesToCounts(topResults);
	}
	
	private void convertEntriesToCounts(List<Entry<String, Float>> entries) {
		List<HashtagScore> hashtagCounts = new ArrayList<>();
		for(Entry<String, Float> entry: entries) {
			hashtagCounts.add(new HashtagScore(entry.getKey(), entry.getValue()));
		}
		results = new AnalysisResults(hashtagCounts);
		log.info("Analysis finished, results are: {}", results);
	}

	@Override
	public AnalysisResults getResults() {
		return results;
	}

}
