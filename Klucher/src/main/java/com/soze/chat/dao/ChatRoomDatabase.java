package com.soze.chat.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.chat.model.ChatRoomEntity;
import com.soze.chat.repository.ChatRoomRepository;

@Service
public class ChatRoomDatabase implements ChatRoomDao {

	private final ChatRoomRepository repository;
	private final EntityManager em;
	
	@Autowired
	public ChatRoomDatabase(ChatRoomRepository repository, EntityManager em) {
		this.repository = repository;
		this.em = em;
	}
	
	@Override
	public ChatRoomEntity findOne(String name) {
		return repository.findByName(name);	
	}
	
	@Override
	public ChatRoomEntity save(ChatRoomEntity chatRoom) {
		return repository.save(chatRoom);
	}
	
	@Override
	public ChatRoomEntity findNewest(String name) {
		String queryString = "SELECT c FROM ChatRoomEntity c WHERE c.name = ?1";
		TypedQuery<ChatRoomEntity> query = em.createQuery(queryString, ChatRoomEntity.class);
		query.setParameter(1, name);
		List<ChatRoomEntity> rooms = query.getResultList();
		if(rooms.isEmpty()) {
			return null;
		}
		return rooms.stream()
			.max((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()))
			.orElse(null);
	}
	
}
