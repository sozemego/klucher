package com.soze.user.model;

/**
 * Object that is meant to be sent as a part of a list of followers. It contains
 * data relevant to displaying a list of followers (username, path to avatar
 * image, etc).
 */
public class UserFollowerView {

	private final String username;
	private final String avatarPath;

	public UserFollowerView(String username, String avatarPath) {
		this.username = username;
		this.avatarPath = avatarPath;
	}

	public String getUsername() {
		return username;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

}
