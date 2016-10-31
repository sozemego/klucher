package com.soze.chat.model;

/**
 * Sent from the client to request list of users for a channel.
 * @author kamil jurek
 *
 */
public class RequestUserList extends InboundSocketMessage {

	public RequestUserList() {
		super(InboundMessageType.USER_LIST_REQUEST);
	}
}
