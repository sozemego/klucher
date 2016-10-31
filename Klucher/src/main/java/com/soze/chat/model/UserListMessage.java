package com.soze.chat.model;

import java.util.Set;

/**
 * Wraps a set of users belonging to a specific channel.
 * @author kamil jurek
 *
 */
public class UserListMessage extends OutboundSocketMessage {

	private Set<String> users;
	
	public UserListMessage() {
		super(OutboundMessageType.USER_LIST);
	}

	public UserListMessage(Set<String> users) {
		super(OutboundMessageType.USER_LIST);
		this.users = users;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

}
