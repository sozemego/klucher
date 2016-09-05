package com.soze.feed.model;

import java.util.List;

public class NotificationRequest {

	private List<Long> ids;

	public NotificationRequest() {

	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

}
