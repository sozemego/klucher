package com.soze.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.chat.model.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>{

	public ChatRoomEntity findByName(String name);
	
}
