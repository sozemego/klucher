package com.soze.chat.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import com.soze.chat.model.ChatMessageBundle;
import com.soze.chat.model.ChatRoom;
import com.soze.chat.model.InboundSocketMessage;
import com.soze.chat.model.InboundSocketMessage.InboundMessageType;
import com.soze.chat.model.OutboundSocketMessage;
import com.soze.chat.model.UserListMessage;
import com.soze.common.exceptions.ChatRoomAlreadyExistsException;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;
import com.soze.common.exceptions.NullOrEmptyException;

@Service
public class ChatService {

	private final ChatRoomContainer roomContainer;
	private final WebSocketMessageBrokerStats stats;
	private final ChatMessageService messageService;
	private final SimpMessagingTemplate simp;
	
	@Autowired
	public ChatService(ChatRoomContainer roomContainer, WebSocketMessageBrokerStats stats, ChatMessageService messageService, SimpMessagingTemplate simp) {
		this.roomContainer = roomContainer;
		this.stats = stats;
		this.messageService = messageService;
		this.simp = simp;		
	}
	
	/**
	 * Attempts to add a user with sessionId and username to a given room. If this
	 * user was not previously present in this room, sends a message to all
	 * subscribers that this user joined this room.
	 * 
	 * @param roomName
	 * @param sessionId
	 * @param username
	 * @throws ChatRoomDoesNotExistException
	 *           if chat room does not exist
	 * @throws NullOrEmptyException
	 *           if any of the paramters are null or empty
	 */
	public void addUser(String roomName, String sessionId, String username)
			throws NullOrEmptyException, ChatRoomDoesNotExistException {
		roomContainer.addUser(roomName, sessionId, username);
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
		roomContainer.removeUser(roomName, sessionId);
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
		roomContainer.addChatRoom(roomName);
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
		roomContainer.removeChatRoom(roomName);
	}
	
	/**
	 * Returns true if a room with a given name exists.
	 * @param roomName
	 * @return
	 * @throws NullOrEmptyException if roomName is null or empty
	 */
	public boolean doesChatRoomExist(String roomName) throws NullOrEmptyException {
		return roomContainer.doesChatRoomExist(roomName);
	}
	
	/**
	 * Constructs an appropriate response to a message from the client
	 * in a given room (roomName).
	 * @param bundle
	 * @param roomName
	 * @return
	 */
	public OutboundSocketMessage respond(ChatMessageBundle bundle, String roomName) {
		InboundSocketMessage message = bundle.getMessage();
		if(message.getType() == InboundMessageType.USER_LIST_REQUEST) {
			return new UserListMessage(getUsers(roomName));
		}
		return messageService.respond(bundle);
	}
	
	/**
	 * Returns a set of usernames connected to a given room.
	 * 
	 * @param roomName
	 * @return
	 * @throws ChatRoomDoesNotExistException
	 *           if chat room with given name does not exist
	 */
	public Set<String> getUsers(String roomName) throws ChatRoomDoesNotExistException {
		return roomContainer.getUsers(roomName);
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
		return roomContainer.getNumberOfUsers(roomName);
	}
	
	/**
	 * Constructs a map of chat room name and user counts.
	 * @return
	 */
	public Map<String, Integer> getUserCounts() {
		return roomContainer.getUserCounts();
	}
	
	@Scheduled(initialDelayString = "${chat.updateusercountinterval}", fixedDelayString = "${chat.updateusercountinterval}")
	public void updateUserCounts() {
		Map<String, Integer> userCounts = roomContainer.getUserCounts();
		for(Map.Entry<String, Integer> entry: userCounts.entrySet()) {
			String roomName = entry.getKey();
			Integer userCount = entry.getValue();
			simp.convertAndSend("/chat/back/" + roomName, new UserCount(userCount));
		}
	}
	
	public static class UserCount extends OutboundSocketMessage {
		
		public Integer userCount;

		UserCount(Integer userCount) {
			super(OutboundMessageType.USER_COUNT);
			this.userCount = userCount;
		}

	}
	
}
