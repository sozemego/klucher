package com.soze.chat.dao;

import com.soze.chat.model.ChatRoomEntity;

public interface ChatRoomDao {

	public ChatRoomEntity findOne(String name);
	
	public ChatRoomEntity findNewest(String name);
	
	public ChatRoomEntity save(ChatRoomEntity chatRoom);
	
}
