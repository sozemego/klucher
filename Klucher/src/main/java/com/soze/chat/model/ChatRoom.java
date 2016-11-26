package com.soze.chat.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	/** A set which contains names of all users who ever joined, not just current users */
	private final Set<String> allUsers = new HashSet<>();
	private final Map<String, String> sessionIdToUser = new HashMap<>();
	private int maxConcurrentUsers = 0;
	private boolean closed;

	public ChatRoom(String name) {
		this(name, LocalDateTime.now());
	}
	
	public ChatRoom(String name, LocalDateTime timeCreated) {
		this.name = name;
		this.timeCreated = timeCreated;
	}

	public Set<String> getUsers() {
		return users;
	}

	/**
	 * Adds a user with username and given session to this chat room. Also maps
	 * given sessionId to username. Returns true if added user was not already in this room.
	 * @param sessionId
	 * @param username
	 * @return true if user was NOT present in this room
	 */
	public boolean addUser(String sessionId, String username) {
		boolean userPreviouslyNotPresent = users.add(username);
		allUsers.add(username);
		sessionIdToUser.put(sessionId, username);
		updateMaxConcurrentUsers();
		return userPreviouslyNotPresent;
	}
	
	private void updateMaxConcurrentUsers() {
		int currentUsers = getUsers().size();
		if(currentUsers > maxConcurrentUsers) {
			maxConcurrentUsers = currentUsers;
		}
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

	public int getMaxConcurrentUsers() {
		return maxConcurrentUsers;
	}

	public void setMaxConcurrentUsers(int maxConcurrentUsers) {
		this.maxConcurrentUsers = maxConcurrentUsers;
	}

	public Set<String> getAllUniqueUsers() {
		return allUsers;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
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
