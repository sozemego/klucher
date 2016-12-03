package com.soze.common.feed;

/**
 * Direction of feed pagination. NEXT signifies going back in time,
 * that is, earlier elements. PREVIOUS signifies going forward in time, that is later elements.
 * @author kamil jurek
 *
 */
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
