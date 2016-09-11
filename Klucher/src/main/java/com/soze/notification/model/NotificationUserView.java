package com.soze.notification.model;

import javax.persistence.Embeddable;

/**
 * An object which contains user data relevant to a {@link Notification}.
 * 
 * @author sozek
 *
 */
@Embeddable
public class NotificationUserView {

	private String username;
	private String avatarPath;

	public NotificationUserView() {

	}

	public NotificationUserView(String username, String avatarPath) {
		this.username = username;
		this.avatarPath = avatarPath;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public String getUsername() {
		return username;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

}
