package com.soze.hashtag.service.analysis;

public class HashtagCount {

	private final String hashtag;
	private final int count;

	public HashtagCount(String hashtag, int count) {
		this.hashtag = hashtag;
		this.count = count;
	}

	public String getHashtag() {
		return hashtag;
	}

	public int getCount() {
		return count;
	}

}
