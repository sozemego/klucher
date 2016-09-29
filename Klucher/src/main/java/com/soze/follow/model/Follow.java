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
	private Long followerId;

	/**
	 * Name of the user who is being followed.
	 */
	@NotNull
	private Long followeeId;

	public Follow() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getFollowerId() {
		return followerId;
	}

	public void setFollowerId(Long followerId) {
		this.followerId = followerId;
	}

	public Long getFolloweeId() {
		return followeeId;
	}

	public void setFolloweeId(Long followeeId) {
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
