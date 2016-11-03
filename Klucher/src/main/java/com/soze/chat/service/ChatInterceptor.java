package com.soze.chat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Service;

@Service
public class ChatInterceptor extends ChannelInterceptorAdapter {
	
	@Autowired
	private ChatService service;
	
	@Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand command = accessor.getCommand();

    if(command == StompCommand.SEND) {
    	String name = getRoomName(accessor);
    	if(!service.isChatRoomOpen(name)) {
    		return null;
    	}
    }
    if(command == StompCommand.SUBSCRIBE) {
    	String sessionID = getSessionId(accessor);
    	String roomName = getRoomName(accessor);
    	String username = getUsername(accessor);
    	service.addUser(roomName, sessionID, username);
    }
    if(command == StompCommand.DISCONNECT) {
    	String roomName = getRoomName(accessor);
    	String sessionId = getSessionId(accessor);
    	service.removeUser(Optional.ofNullable(roomName), sessionId);
    }
    
    return message;
  }
	
	private String getRoomName(StompHeaderAccessor accessor) {
		String destination = accessor.getDestination();
		if(destination == null) {
			return null;
		}
  	String[] tokens = destination.split("/");
  	String hashtag = tokens[tokens.length - 1];
  	return hashtag;
	}
	
	private String getSessionId(StompHeaderAccessor accessor) {
		String sessionId = accessor.getSessionId();
		return sessionId;
	}
	
	private String getUsername(StompHeaderAccessor accessor) {
		return accessor.getUser().getName();
	}
}
