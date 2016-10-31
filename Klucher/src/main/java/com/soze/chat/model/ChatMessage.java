package com.soze.chat.model;

/**
 * Message received from the client.
 * @author kamil jurek
 *
 */
public class ChatMessage extends InboundSocketMessage {

	private String content;
	
	public ChatMessage() {
		super(InboundMessageType.CHAT_MESSAGE);
	}

	public ChatMessage(String content) {
		super(InboundMessageType.CHAT_MESSAGE);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Content: [" + content + "].";
	}

}
