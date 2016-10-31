package com.soze.chat.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.soze.chat.model.ChatMessageBundle;
import com.soze.chat.model.InboundSocketMessage;
import com.soze.chat.model.OutboundSocketMessage;
import com.soze.chat.service.ChatService;

@Controller
public class ChatController {
	
	private final ChatService chatService;
	
	@Autowired
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}
	
	@MessageMapping("/in/{roomName}")
	@SendTo("/chat/back/{roomName}")
	public OutboundSocketMessage chat(@DestinationVariable String roomName,
			InboundSocketMessage message,
			Principal principal,
			SimpMessageHeaderAccessor headerAccessor) throws Exception {
		
		return chatService.respond(new ChatMessageBundle(message, principal, headerAccessor), roomName);
	}
	
	@RequestMapping(value = "/chat/{roomName}", method = RequestMethod.GET)
	public String getChatHashtag(Authentication authentication, @PathVariable String roomName, Model model) {
		if(chatService.doesChatRoomExist(roomName)) {
			model.addAttribute("hashtag", roomName);
		}
		return "chat";
	}
	
	@RequestMapping(value = "/chat", method = RequestMethod.GET)
	public String getChat(Authentication authentication) {
		return "chat";
	}	
	
}
