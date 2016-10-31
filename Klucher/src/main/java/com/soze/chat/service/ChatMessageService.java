package com.soze.chat.service;

import org.springframework.stereotype.Service;

import com.soze.chat.model.ChatMessage;
import com.soze.chat.model.ChatMessageBundle;
import com.soze.chat.model.ChatResponse;
import com.soze.common.exceptions.NullOrEmptyException;

@Service
public class ChatMessageService {
	
	private static final int MAX_MESSAGE_LENGTH = 140;

	/**
	 * Constructs a ChatResponse to a given message. Does basic message content
	 * validation, but throws no exceptions (truncates input instead).
	 * 
	 * @param bundle
	 * @return
	 * @throws NullOrEmptyException
	 *           if bundle is null
	 */
	public ChatResponse respond(ChatMessageBundle bundle) throws NullOrEmptyException {
		if (bundle == null) {
			throw new NullOrEmptyException("Message bundle");
		}
		long currentTime = System.currentTimeMillis();
		String username = bundle.getPrincipal().getName();
		ChatMessage chatMessage = (ChatMessage) bundle.getMessage();
		String message = truncateMessage(chatMessage.getContent());
		return new ChatResponse(currentTime, username, message);
	}
	
	private String truncateMessage(String message) {
		message = message.trim();
		if(message.length() > MAX_MESSAGE_LENGTH) {
			return message.substring(0, MAX_MESSAGE_LENGTH);
		}
		return message;
	}
	
}
