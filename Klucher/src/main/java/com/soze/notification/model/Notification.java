package com.soze.notification.model;

import javax.persistence.Embeddable;

@Embeddable
public class Notification {

	private Long kluchId;

	// name of the user which was followed by another user
	private String follow;

	private boolean read;

	public Notification() {

	}

	public Long getKluchId() {
		return kluchId;
	}

	public void setKluchId(Long kluchId) {
		this.kluchId = kluchId;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	/**
	 * Name of the user who was followed.
	 * @return
	 */
	public String getFollow() {
		return follow;
	}

	/**
	 * Sets name of the user who was followed.
	 * @param follow
	 */
	public void setFollow(String follow) {
		this.follow = follow;
	}

}
