package com.soze.chat.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.soze.chat.model.ChatRoom;
import com.soze.chat.model.NewUserMessage;
import com.soze.chat.model.RemoveUserMessage;
import com.soze.common.exceptions.ChatRoomAlreadyExistsException;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;
import com.soze.common.exceptions.NullOrEmptyException;

@Service
public class ChatRoomContainer {

	private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
	
	@Autowired
	private SimpMessagingTemplate simp;
	
	@PostConstruct
	public void init() {
		this.chatRooms.put("doge", new ChatRoom("doge"));
		this.chatRooms.put("antyglowa", new ChatRoom("antyglowa"));
	}
	
	/**
	 * Attempts to add a user with sessionId and username to a given room. If this
	 * user was not previously present in this room, sends a message to all
	 * subscribers that this user joined this room.
	 * 
	 * @param roomName
	 * @param sessionId
	 * @param username
	 * @throws NullOrEmptyException
	 *           if any of the paramters are null or empty
	 */
	public void addUser(String roomName, String sessionId, String username)
			throws NullOrEmptyException {
		validateInput(roomName, sessionId, username);
		if (doesChatRoomExist(roomName)) {
			boolean newUser = chatRooms.get(roomName).addUser(sessionId, username);
			if (newUser) {
				announceNewUser(roomName, username);
			}
		}
	}
	
	/**
	 * Removes user associated with given sessionId. If roomName is null, searches
	 * all rooms to find a given user. Otherwise, attempts to remove this user
	 * from a given room. If user was present in the room and was removed sends a
	 * message to all subscribers that a username associated with given sessionId
	 * was removed.
	 * 
	 * @param roomName
	 * @param sessionId
	 * @throws NullOrEmptyException
	 *           if sessionId is null or empty
	 */
	public void removeUser(String roomName, String sessionId) throws NullOrEmptyException {
		validateInput(sessionId);
		String username = null;
		if (roomName == null) {
			for (ChatRoom room : chatRooms.values()) {
				username = room.removeUser(sessionId);
				roomName = room.getName();
				break;
			}
		} else if (doesChatRoomExist(roomName)) {
			username = chatRooms.get(roomName).removeUser(sessionId);
		}
		if (username != null) {
			announceUserRemoval(roomName, username);
		}
	}
	
	/**
	 * Attempts to create a room.
	 * 
	 * @param roomName
	 * @throws ChatRoomAlreadyExistsException
	 *           if room with this name already exists
	 * @throws NullOrEmptyException
	 *           if roomName is null or empty
	 */
	public void addChatRoom(String roomName) throws ChatRoomAlreadyExistsException, NullOrEmptyException {
		validateInput(roomName);
		if (doesChatRoomExist(roomName)) {
			throw new ChatRoomAlreadyExistsException(roomName);
		} else {
			chatRooms.put(roomName, new ChatRoom(roomName));
		}
	}
	
	/**
	 * Removes a chat room with given name.
	 * 
	 * @param roomName
	 * @throws ChatRoomDoesNotExistException
	 *           if chat room with name roomName does not exist
	 * @throws NullOrEmptyException if roomName is null or empty
	 */
	public void removeChatRoom(String roomName) throws ChatRoomDoesNotExistException, NullOrEmptyException {
		validateInput(roomName);
		if (chatRooms.remove(roomName) == null) {
			throw new ChatRoomDoesNotExistException(roomName);
		}
	}
	
	/**
	 * Returns true if a room with a given name exists.
	 * @param roomName
	 * @return
	 * @throws NullOrEmptyException if roomName is null or empty
	 */
	public boolean doesChatRoomExist(String roomName) throws NullOrEmptyException {
		validateInput(roomName);
		return chatRooms.containsKey(roomName);
	}
	
	/**
	 * Returns a number of users currently connected to a given chat room.
	 * 
	 * @param roomName
	 * @return
	 * @throws ChatRoomDoesNotExistException
	 *           if given chat room does not exist
	 * @throws NullOrEmptyException if roomName is null or empty
	 */
	public int getNumberOfUsers(String roomName) throws ChatRoomDoesNotExistException, NullOrEmptyException {
		return getUsers(roomName).size();
	}
	
	/**
	 * Constructs a map of chat room name and user counts.
	 * @return
	 */
	public Map<String, Integer> getUserCounts() {
		Map<String, Integer> userCounts = new HashMap<>();
		for(String roomName: chatRooms.keySet()) {
			userCounts.put(roomName, getNumberOfUsers(roomName));
		}
		return userCounts;
	}

	/**
	 * Returns a set of usernames connected to a given room.
	 * 
	 * @param roomName
	 * @return
	 * @throws ChatRoomDoesNotExistException
	 *           if chat room with given name does not exist
	 * @throws NullOrEmptyException if roomName is null or empty
	 */
	public Set<String> getUsers(String roomName) throws ChatRoomDoesNotExistException, NullOrEmptyException {
		validateInput(roomName);
		if (doesChatRoomExist(roomName)) {
			ChatRoom room = chatRooms.get(roomName);
			return room.getUsers();
		}
		throw new ChatRoomDoesNotExistException(roomName);
	}
	
	private void announceNewUser(String roomName, String username) {
		simp.convertAndSend("/chat/back/" + roomName, new NewUserMessage(username));
	}
	
	private void announceUserRemoval(String roomName, String username) {
		simp.convertAndSend("/chat/back/" + roomName, new RemoveUserMessage(username));
	}
	
	private void validateInput(String... strings) throws NullOrEmptyException {
		for(String str: strings) {
			if(str == null || str.isEmpty()) {
				throw new NullOrEmptyException("Chat parameter");
			}
		}
	}
	
}
