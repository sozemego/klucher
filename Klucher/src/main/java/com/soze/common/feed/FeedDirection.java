package com.soze.common.feed;

public enum FeedDirection {

	NEXT(Long.MAX_VALUE), PREVIOUS(0L);

	private final long defaultId;

	private FeedDirection(long defaultId) {
		this.defaultId = defaultId;
	}

	public long getDefaultId() {
		return defaultId;
	}

}
