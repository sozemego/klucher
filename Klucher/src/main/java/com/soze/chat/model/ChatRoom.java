package com.soze.chat.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An object representing one chat room.
 * @author kamil jurek
 *
 */
public class ChatRoom {

	private final String name;
	private final LocalDateTime timeCreated;
	private final Set<String> users = new HashSet<>();
	private final Map<String, String> sessionIdToUser = new HashMap<>();

	public ChatRoom(String name) {
		this.name = Objects.requireNonNull(name);
		this.timeCreated = LocalDateTime.now();
	}

	public Set<String> getUsers() {
		return users;
	}

	public boolean addUser(String sessionId, String username) {
		boolean userPreviouslyNotPresent = users.add(username);
		sessionIdToUser.put(sessionId, username);
		return userPreviouslyNotPresent;
	}

	public String removeUser(String sessionID) {
		String username = sessionIdToUser.remove(sessionID);
		users.remove(username);
		return username;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getTimeCreated() {
		return timeCreated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatRoom other = (ChatRoom) obj;
		if (!name.equals(other.name))
			return false;
		return true;
	}

}
