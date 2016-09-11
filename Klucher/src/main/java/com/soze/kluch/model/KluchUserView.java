package com.soze.kluch.model;

/**
 * A class containing fields of {@link User} object which are relevant to send
 * among {@link Kluch}'s.
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
