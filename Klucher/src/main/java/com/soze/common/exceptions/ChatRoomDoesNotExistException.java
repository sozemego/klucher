package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class ChatRoomDoesNotExistException extends RuntimeException {

	private final String roomName;

	public ChatRoomDoesNotExistException(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomName() {
		return roomName;
	}

}
