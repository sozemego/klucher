package com.soze.chat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatList {

	private final List<ChatRoomCount> chatRoomCounts = new ArrayList<>();

	public ChatList(Map<String, Integer> userCounts) {
		getChatRoomCounts(userCounts);
	}

	public List<ChatRoomCount> getChatRoomCounts() {
		return chatRoomCounts;
	}

	private void getChatRoomCounts(Map<String, Integer> userCounts) {
		for (Map.Entry<String, Integer> entry : userCounts.entrySet()) {
			String roomName = entry.getKey();
			Integer userCount = entry.getValue();
			chatRoomCounts.add(new ChatRoomCount(roomName, userCount));
		}
	}

}
