package com.soze.chat.model;

public class ChatRoomCount {

	private final String roomName;
	private final int userCount;

	public ChatRoomCount(String roomName, int userCount) {
		this.roomName = roomName;
		this.userCount = userCount;
	}

	public String getRoomName() {
		return roomName;
	}

	public int getUserCount() {
		return userCount;
	}

}
