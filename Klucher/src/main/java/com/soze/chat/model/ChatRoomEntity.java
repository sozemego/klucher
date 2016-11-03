package com.soze.chat.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "chat_rooms")
public class ChatRoomEntity {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private Timestamp createdAt;

	private Timestamp closedAt;

	private int uniqueUsers;
	private int maxConcurrentUsers;

	public ChatRoomEntity() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(Timestamp closedAt) {
		this.closedAt = closedAt;
	}

	public int getUniqueUsers() {
		return uniqueUsers;
	}

	public void setUniqueUsers(int uniqueUsers) {
		this.uniqueUsers = uniqueUsers;
	}

	public int getMaxConcurrentUsers() {
		return maxConcurrentUsers;
	}

	public void setMaxConcurrentUsers(int maxConcurrentUsers) {
		this.maxConcurrentUsers = maxConcurrentUsers;
	}
	
	public boolean isOpen() {
		return closedAt != null;
	}
	
	public boolean isClosed() {
		return !isOpen();
	}

}
