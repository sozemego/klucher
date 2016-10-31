package com.soze.chat.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soze.chat.service.ChatService.UserCount;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = ChatResponse.class, name = "CHAT_MESSAGE"),
    @Type(value = UserCount.class, name = "USER_COUNT"),
    @Type(value = NewUserMessage.class, name = "ADD_USER"),
    @Type(value = RemoveUserMessage.class, name = "REMOVE_USER"),
    @Type(value = UserListMessage.class, name = "USER_LIST")})
public abstract class OutboundSocketMessage {

	private final OutboundMessageType type;	
	
	public OutboundSocketMessage(OutboundMessageType type) {
		this.type = type;
	}

	public OutboundMessageType getType() {
		return type;
	}
	
	public enum OutboundMessageType {
		/** to client */
		CHAT_MESSAGE,
		USER_COUNT,
		ADD_USER,
		REMOVE_USER,
		USER_LIST;
	}
	
}
