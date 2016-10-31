package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class ChatRoomAlreadyExistsException extends RuntimeException {

	private final String hashtag;

	public ChatRoomAlreadyExistsException(String hashtag) {
		super();
		this.hashtag = hashtag;
	}
	
	public String getHashtag() {
		return hashtag;
	}

}
