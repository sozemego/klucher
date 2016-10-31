package com.soze.chat.model;

import java.security.Principal;
import java.util.Objects;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class ChatMessageBundle {

	private final InboundSocketMessage message;
	private final Principal principal;
	private final SimpMessageHeaderAccessor headerAccessor;

	public ChatMessageBundle(InboundSocketMessage message, Principal principal, SimpMessageHeaderAccessor headerAccessor) {
		this.message = Objects.requireNonNull(message);
		this.principal = Objects.requireNonNull(principal);
		this.headerAccessor = Objects.requireNonNull(headerAccessor);
	}

	public InboundSocketMessage getMessage() {
		return message;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public SimpMessageHeaderAccessor getHeaderAccessor() {
		return headerAccessor;
	}

}
