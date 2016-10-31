package com.soze.chat.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = ChatMessage.class, name = "CHAT_MESSAGE"),
    @Type(value = RequestUserList.class, name = "USER_LIST_REQUEST") })
public abstract class InboundSocketMessage {

	private final InboundMessageType type;

	public InboundSocketMessage(InboundMessageType type) {
		this.type = type;
	}

	public InboundMessageType getType() {
		return type;
	}

	public enum InboundMessageType {
		/** to server */
		CHAT_MESSAGE, USER_LIST_REQUEST;
	}
}
