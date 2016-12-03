package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class AlreadyFollowsException extends RuntimeException {

	private final String followerName;
	private final String followeeName;

	public AlreadyFollowsException(String followerName, String followeeName) {
		super();
		this.followerName = followerName;
		this.followeeName = followeeName;
	}

	public String getFollowerName() {
		return followerName;
	}

	public String getFolloweeName() {
		return followeeName;
	}

}
