package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class ChatRoomDoesNotExistException extends RuntimeException {

	private final String hashtag;

	public ChatRoomDoesNotExistException(String hashtag) {
		this.hashtag = hashtag;
	}

	public String getHashtag() {
		return hashtag;
	}

}
