package com.soze.user.model;

/**
 * Object that is meant to be sent as a part of a list of followers. It contains
 * data relevant to displaying a list of users that like another user.
 *
 * @author kamil jurek
 *
 */
public class UserLikeView {
	
	private final String username;
	private final String avatarPath;

	public UserLikeView(String username, String avatarPath) {
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
