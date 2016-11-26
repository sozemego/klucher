package com.soze.chat.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.chat.model.ChatList;
import com.soze.chat.model.ChatMessageBundle;
import com.soze.chat.model.InboundSocketMessage;
import com.soze.chat.model.OutboundSocketMessage;
import com.soze.chat.service.ChatService;

@Controller
public class ChatController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);
	
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
		if(chatService.isChatRoomOpen(roomName)) {
			model.addAttribute("hashtag", roomName);
		}
		return "chat";
	}
	
	@RequestMapping(value = "/chat", method = RequestMethod.GET)
	public String getChat() {
		return "chat";
	}	
	
	@RequestMapping(value = "/chats/trending", method = RequestMethod.GET)
	@ResponseBody
	public ChatList getTrendingChats() {
		return new ChatList(chatService.getUserCounts());
	}
	
	@RequestMapping(value = "/chats/trigger", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerOpenRooms(Authentication authentication) {
		LOG.info("[{}] requested to open chat rooms.", authentication.getName());
  	chatService.openRooms();
  	return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/chats/close/{roomName}", method = RequestMethod.POST)
	public ResponseEntity<Object> closeRoom(Authentication authentication, @PathVariable("roomName") String roomName) {
		LOG.info("[{}] requested to close chat room named [{}]", authentication.getName(), roomName);
  	chatService.removeChatRoom(roomName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
