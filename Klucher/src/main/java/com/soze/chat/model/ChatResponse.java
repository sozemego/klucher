package com.soze.chat.model;

/**
 * Response sent to the client to a ChatMessage.
 * @author kamil jurek
 *
 */
public class ChatResponse extends OutboundSocketMessage {

	private long timestamp;
	private String username;
	private String message;
	
	public ChatResponse() {
		super(OutboundMessageType.CHAT_MESSAGE);
	}

	public ChatResponse(long timestamp, String username, String message) {
		super(OutboundMessageType.CHAT_MESSAGE);
		this.timestamp = timestamp;
		this.username = username;
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() {
		return message;
	}

}
