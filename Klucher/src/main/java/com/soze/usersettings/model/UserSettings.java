package com.soze.usersettings.model;

import javax.persistence.Embeddable;

@Embeddable
public class UserSettings {

	private String avatarPath;

	private Integer kluchsPerRequest;

	@SuppressWarnings("unused")
	private UserSettings() {

	}

	public UserSettings(String avatarPath, Integer kluchsPerRequest) {
		this.avatarPath = avatarPath;
		this.kluchsPerRequest = kluchsPerRequest;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public Integer getKluchsPerRequest() {
		return kluchsPerRequest;
	}

	public void setKluchsPerRequest(Integer kluchsPerRequest) {
		this.kluchsPerRequest = kluchsPerRequest;
	}

}
