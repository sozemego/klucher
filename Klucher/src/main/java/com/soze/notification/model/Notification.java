package com.soze.notification.model;

import javax.persistence.Embeddable;

@Embeddable
public class Notification {

	private Long kluchId;

	// user data relevant to this notification
	private NotificationUserView notificationUserView;

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

	public NotificationUserView getNotificationUserView() {
		return notificationUserView;
	}

	public void setNotificationUserView(NotificationUserView notificationUserView) {
		this.notificationUserView = notificationUserView;
	}

}
