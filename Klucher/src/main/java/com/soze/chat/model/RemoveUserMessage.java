package com.soze.chat.model;

/**
 * Signifies a user leaving a channel.
 * @author kamil jurek
 *
 */
public class RemoveUserMessage extends OutboundSocketMessage {

	private final String username;

	public RemoveUserMessage(String username) {
		super(OutboundMessageType.REMOVE_USER);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
}
