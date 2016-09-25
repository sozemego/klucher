package com.soze.user.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.soze.notification.model.FollowNotification;
import com.soze.notification.model.MentionNotification;

@SuppressWarnings("serial")
@Entity
public class User implements UserDetails {

	@Id
	@Size(min = 1, max = 32)
	private String username;

	@NotNull
	private String hashedPassword;

	@OneToOne(cascade = { CascadeType.ALL })
	@NotNull
	private UserRoles userRoles;

	@ElementCollection
	private Set<String> followers = new HashSet<>();
	@ElementCollection
	private Set<String> following = new HashSet<>();

	@ElementCollection
	@OrderBy("timestamp DESC")
	private List<MentionNotification> mentionNotifications = new ArrayList<>();

	@ElementCollection
	@OrderBy("timestamp DESC")
	private List<FollowNotification> followNotifications = new ArrayList<>();

	private String avatarPath;

	public User() {

	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public UserRoles getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(UserRoles userRoles) {
		this.userRoles = userRoles;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Names of users that follow this user.
	 * 
	 * @return
	 */
	public Set<String> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<String> followers) {
		this.followers = followers;
	}

	/**
	 * Names of users this user follows.
	 * 
	 * @return
	 */
	public Set<String> getFollowing() {
		return following;
	}

	public void setFollowing(Set<String> following) {
		this.following = following;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public List<MentionNotification> getMentionNotifications() {
		return mentionNotifications;
	}

	public void setMentionNotifications(List<MentionNotification> mentionNotifications) {
		this.mentionNotifications = mentionNotifications;
	}

	public List<FollowNotification> getFollowNotifications() {
		return followNotifications;
	}

	public void setFollowNotifications(List<FollowNotification> followNotifications) {
		this.followNotifications = followNotifications;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = new ArrayList<>();
		if (userRoles.isUser()) {
			list.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		if (userRoles.isAdmin()) {
			list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return list;
	}

	@Override
	public String getPassword() {
		return hashedPassword;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public boolean equals(Object second) {
		if (second == null) {
			return false;
		}
		if (this == second) {
			return true;
		}
		if (second instanceof User) {
			return username.equals(((User) second).username);
		}
		return false;
	}

}
