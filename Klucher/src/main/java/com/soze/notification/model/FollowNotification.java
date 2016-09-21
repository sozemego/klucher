package com.soze.notification.model;

import javax.persistence.Embeddable;

@Embeddable
public class FollowNotification extends Notification {

	/**
	 * Name of the user who followed another user and path to their avatar
	 * (profile image).
	 */
	private String username;
	private String avatarPath;

	private boolean noticed;

	public FollowNotification() {

	}

	public FollowNotification(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public boolean isNoticed() {
		return noticed;
	}

	public void setNoticed(boolean noticed) {
		this.noticed = noticed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FollowNotification other = (FollowNotification) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
