package com.soze.kluch.model;

import com.soze.common.feed.FeedDirection;

public class FeedRequest {

	private final FeedDirection feedDirection;
	private final Long id;

	public FeedRequest(FeedDirection feedDirection, Long id) {
		this.feedDirection = feedDirection;
		this.id = id;
	}

	public FeedDirection getFeedDirection() {
		return feedDirection;
	}

	public Long getId() {
		if (id == null) {
			return feedDirection.getDefaultId();
		}
		return id;
	}

	@Override
	public String toString() {
		return "Feed direction [" + feedDirection + "]. Id [" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feedDirection == null) ? 0 : feedDirection.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FeedRequest other = (FeedRequest) obj;
		if (feedDirection != other.feedDirection)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
