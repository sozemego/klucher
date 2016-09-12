package com.soze.kluch.model;

/**
 * A class containing relevant data about a single {@link Kluch}'s author. Used
 * within {@link KluchFeedElement}.
 * 
 * @author sozek
 *
 */
public class KluchUserView {

	private final String username;
	private final String avatarPath;

	public KluchUserView(String username, String avatarPath) {
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
