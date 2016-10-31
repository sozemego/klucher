package com.soze.chat.model;

/**
 * Signifies a new user joining the channel.
 * @author kamil jurek
 *
 */
public class NewUserMessage extends OutboundSocketMessage {

	private String username;

	public NewUserMessage() {
		super(OutboundMessageType.ADD_USER);
	}

	public NewUserMessage(String username) {
		super(OutboundMessageType.ADD_USER);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
