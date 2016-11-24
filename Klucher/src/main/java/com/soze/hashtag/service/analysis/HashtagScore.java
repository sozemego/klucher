package com.soze.hashtag.service.analysis;

public class HashtagScore {

	private final String hashtag;
	private final float score;

	public HashtagScore(String hashtag, float score) {
		this.hashtag = hashtag;
		this.score = score;
	}

	public String getHashtag() {
		return hashtag;
	}

	public float getScore() {
		return score;
	}

}
