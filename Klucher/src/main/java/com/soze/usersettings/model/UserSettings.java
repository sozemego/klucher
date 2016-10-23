package com.soze.usersettings.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserSettings {

	@NotNull
	private String avatarPath;

	@NotNull
	private Integer kluchsPerRequest;

	@Max(140)
	@NotNull
	private String profileDescription;

	@SuppressWarnings("unused")
	private UserSettings() {

	}

	public UserSettings(String avatarPath, Integer kluchsPerRequest, String profileDescription) {
		this.avatarPath = avatarPath;
		this.kluchsPerRequest = kluchsPerRequest;
		this.profileDescription = profileDescription;
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

	public String getProfileDescription() {
		return profileDescription;
	}

	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avatarPath == null) ? 0 : avatarPath.hashCode());
		result = prime * result + ((kluchsPerRequest == null) ? 0 : kluchsPerRequest.hashCode());
		result = prime * result + ((profileDescription == null) ? 0 : profileDescription.hashCode());
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
		UserSettings other = (UserSettings) obj;
		if (avatarPath == null) {
			if (other.avatarPath != null)
				return false;
		} else if (!avatarPath.equals(other.avatarPath))
			return false;
		if (kluchsPerRequest == null) {
			if (other.kluchsPerRequest != null)
				return false;
		} else if (!kluchsPerRequest.equals(other.kluchsPerRequest))
			return false;
		if (profileDescription == null) {
			if (other.profileDescription != null)
				return false;
		} else if (!profileDescription.equals(other.profileDescription))
			return false;
		return true;
	}

}
