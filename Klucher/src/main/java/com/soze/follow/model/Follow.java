package com.soze.follow.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * An entity representing a follower/followee relationship.
 * 
 * @author sozek
 *
 */
@Entity
@Table(name = "follows")
public class Follow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * Name of the user who follows.
	 */
	@NotNull
	private long followerId;

	/**
	 * Name of the user who is being followed.
	 */
	@NotNull
	private long followeeId;

	@SuppressWarnings("unused")
	private Follow() {

	}
	
	public Follow(long followerId, long followeeId) {
		this.followerId = followerId;
		this.followeeId = followeeId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFollowerId() {
		return followerId;
	}

	public void setFollowerId(long followerId) {
		this.followerId = followerId;
	}

	public long getFolloweeId() {
		return followeeId;
	}

	public void setFolloweeId(long followeeId) {
		this.followeeId = followeeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Follow other = (Follow) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
