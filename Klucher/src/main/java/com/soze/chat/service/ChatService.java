package com.soze.chat.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import com.soze.chat.dao.ChatRoomDao;
import com.soze.chat.model.ChatMessageBundle;
import com.soze.chat.model.ChatResponse;
import com.soze.chat.model.ChatRoom;
import com.soze.chat.model.ChatRoomEntity;
import com.soze.chat.model.InboundSocketMessage;
import com.soze.chat.model.InboundSocketMessage.InboundMessageType;
import com.soze.chat.model.NewUserMessage;
import com.soze.chat.model.OutboundSocketMessage;
import com.soze.chat.model.RemoveUserMessage;
import com.soze.chat.model.UserListMessage;
import com.soze.common.exceptions.ChatRoomAlreadyOpenException;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.hashtag.service.HashtagAnalysisService;
import com.soze.hashtag.service.analysis.AnalysisResults;
import com.soze.hashtag.service.analysis.HashtagScore;

@Service
public class ChatService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
	private final ExecutorService executor = Executors.newCachedThreadPool();
	
	@Autowired
	private WebSocketMessageBrokerStats stats;
	@Autowired
	private ChatMessageService messageService;
	@Autowired
	private SimpMessagingTemplate simp;
	@Autowired
	private ChatRoomDao chatRoomDao;
	
	@Autowired
	private HashtagAnalysisService hashtagAnalysis;
	
	private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
	
	/**
	 * Attempts to create a room.
	 * 
	 * @param roomName
	 * @throws ChatRoomAlreadyOpenException
	 *           if room with this name is already open
	 * @throws NullOrEmptyException
	 *           if roomName is null or empty
	 */
	public void addChatRoom(String roomName) throws ChatRoomAlreadyOpenException, NullOrEmptyException {
		validateInput(roomName);
		if(!isChatRoomOpen(roomName)) {
			openChatRoom(roomName);
		} else {
			throw new ChatRoomAlreadyOpenException(roomName);
		}
	}
	
	private void openChatRoom(String roomName) {
		ChatRoomEntity entity = createPersistentChatRoom(roomName);
		createInMemoryChatRoom(entity);
	}
	
	private ChatRoomEntity createPersistentChatRoom(String roomName) {
		ChatRoomEntity chatRoom = new ChatRoomEntity();
		chatRoom.setName(roomName);
		chatRoom.setCreatedAt(new Timestamp(Instant.now().toEpochMilli()));
		return chatRoomDao.save(chatRoom);
	}
	
	private void createInMemoryChatRoom(ChatRoomEntity entity) {
		chatRooms.put(entity.getName(), new ChatRoom(entity.getName(), entity.getCreatedAt().toLocalDateTime()));
	}
	
	public boolean isChatRoomOpen(String roomName) {
		return chatRooms.containsKey(roomName);
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
		if(!isChatRoomOpen(roomName)) {
			throw new ChatRoomDoesNotExistException(roomName);
		}
		closeChatRoom(roomName);
	}
	
	private void closeChatRoom(String roomName) {
		
		Queue<ScheduledMessage> closureMessages = new LinkedList<>(
				Arrays.asList(new ScheduledMessage("Chat room closing in 1 minute", 1000 * 30),
						new ScheduledMessage("Chat room closing in 30 seconds", 1000 * 20),
						new ScheduledMessage("Chat room closing in 10 seconds", 1000 * 10),
						new ScheduledMessage("Chat room closed.", 0L)));
		
		
		executor.submit(new ChatRoomCloser(closureMessages, roomName, "System"));

	}
	
	private class ChatRoomCloser implements Runnable {
		
		private final Queue<ScheduledMessage> messages;
		private final String roomName;
		private final String userName;
		
		public ChatRoomCloser(Queue<ScheduledMessage> messages, String roomName, String userName) {
			this.messages = messages;
			this.roomName = roomName;
			this.userName = userName;
		}
		
		public void run() {
			while (!messages.isEmpty()) {
				ScheduledMessage message = messages.poll();
				sendMessageFrom(userName, roomName, message.getMessage());
				try {
					Thread.sleep(message.getMillisecondsToWait());
				} catch (InterruptedException e) {

				}
			}
			closeRoom();
		}
		
		private void closeRoom() {
			ChatRoom room = chatRooms.remove(roomName);
			ChatRoomEntity entity = chatRoomDao.findNewest(roomName);
			entity.setClosedAt(new Timestamp(Instant.now().toEpochMilli()));
			entity.setUniqueUsers(room.getAllUniqueUsers().size());
			entity.setMaxConcurrentUsers(room.getMaxConcurrentUsers());
			chatRoomDao.save(entity);
		}
	}
	
	private static class ScheduledMessage {
		private final String message;
		private final long millisecondsToWait;

		public ScheduledMessage(String message, long millisecondsToWait) {
			super();
			this.message = message;
			this.millisecondsToWait = millisecondsToWait;
		}

		public String getMessage() {
			return message;
		}

		public long getMillisecondsToWait() {
			return millisecondsToWait;
		}

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
		validateInput(roomName, sessionId, username);
		if(!isChatRoomOpen(roomName)) {
			throw new ChatRoomDoesNotExistException(roomName);		
		}
		boolean newUser = chatRooms.get(roomName).addUser(sessionId, username);
		if (newUser) {
			announceNewUser(roomName, username);
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
	public void removeUser(Optional<String> roomName, String sessionId) throws NullOrEmptyException {
		validateInput(sessionId);
		String username = null;
		if (!roomName.isPresent()) {
			for (ChatRoom room : chatRooms.values()) {
				username = room.removeUser(sessionId);
				roomName = Optional.of(room.getName());
				break;
			}
		} else if (isChatRoomOpen(roomName.get())) {
			username = chatRooms.get(roomName.get()).removeUser(sessionId);
		}
		if (username != null) {
			announceUserRemoval(roomName.get(), username);
		}
	}
	
	private void announceUserRemoval(String roomName, String username) {
		simp.convertAndSend("/chat/back/" + roomName, new RemoveUserMessage(username));
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
		validateInput(roomName);
		if (isChatRoomOpen(roomName)) {
			ChatRoom room = chatRooms.get(roomName);
			return room.getUsers();
		}
		throw new ChatRoomDoesNotExistException(roomName);
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
		Map<String, Integer> map = new HashMap<>();
		for(Map.Entry<String, ChatRoom> entry: chatRooms.entrySet()) {
			String roomName = entry.getKey();
			ChatRoom room = entry.getValue();
			map.put(roomName, room.getUsers().size());
		}
		return map;
	}
	
	private void announceNewUser(String roomName, String username) {
		simp.convertAndSend("/chat/back/" + roomName, new NewUserMessage(username));
	}
	
	private void sendMessageFrom(String userName, String roomName, String message) {
		simp.convertAndSend("/chat/back/" + roomName, new ChatResponse(Instant.now().toEpochMilli(), userName, message));
	}
	
	@Scheduled(initialDelayString = "${chat.updateusercount.initialdelay}", fixedDelayString = "${chat.updateusercount.interval}")
	public void updateUserCounts() {
		Map<String, Integer> userCounts = getUserCounts();
		userCounts.forEach((name, count) -> {
			simp.convertAndSend("/chat/back/" + name, new UserCount(count));
		});
	}

	@Scheduled(initialDelayString = "${chat.roomcleanup.initialdelay}", fixedDelayString = "${chat.roomcleanup.interval}")
	public void openRooms() {
		AnalysisResults results = hashtagAnalysis.getResults();
		if(results == null) {
			return;
		}
		List<HashtagScore> counts = results.getHashtagScores();
		if(counts.isEmpty()) {
			return;
		}
		Set<String> hashtagNames = counts.stream()
				.map(hc -> hc.getHashtag())
				.collect(Collectors.toSet());
		
		Set<String> roomsToClose = new HashSet<>(chatRooms.keySet());
		roomsToClose.removeAll(hashtagNames);
		
		Set<String> roomsToOpen = new HashSet<>(hashtagNames);
		roomsToOpen.removeAll(chatRooms.keySet());
		
		for(String room: roomsToClose) {
			closeChatRoom(room);
		}
		for(String room: roomsToOpen) {
			openChatRoom(room);
		}
		LOG.info("Closing: {}. Opening: {}", roomsToClose, roomsToOpen);
	}
	
	public static class UserCount extends OutboundSocketMessage {
		
		public Integer userCount;

		UserCount(Integer userCount) {
			super(OutboundMessageType.USER_COUNT);
			this.userCount = userCount;
		}

	}
	
	private void validateInput(String... strings) throws NullOrEmptyException {
		for(String str: strings) {
			if(str == null || str.isEmpty()) {
				throw new NullOrEmptyException("Chat parameter");
			}
		}
	}
	
}
