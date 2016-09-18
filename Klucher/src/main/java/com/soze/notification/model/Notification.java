package com.soze.notification.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Notification {

	private Long kluchId;

	// user data relevant to this notification
	private NotificationUserView notificationUserView;

	@NotNull
	private boolean noticed;

	public Notification() {

	}

	public Long getKluchId() {
		return kluchId;
	}

	public void setKluchId(Long kluchId) {
		this.kluchId = kluchId;
	}

	public void setNoticed(boolean noticed) {
		this.noticed = noticed;
	}

	public boolean isNoticed() {
		return noticed;
	}

	public NotificationUserView getNotificationUserView() {
		return notificationUserView;
	}

	public void setNotificationUserView(NotificationUserView notificationUserView) {
		this.notificationUserView = notificationUserView;
	}

}
