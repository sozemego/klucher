package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class ChatRoomAlreadyOpenException extends RuntimeException {

	private final String roomName;

	public ChatRoomAlreadyOpenException(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomName() {
		return roomName;
	}

}
