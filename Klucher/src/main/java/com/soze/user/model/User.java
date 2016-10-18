package com.soze.user.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User implements UserDetails, Serializable {
	
	private static final long serialVersionUID = 6539127499242225817L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Size(min = 1, max = 32)
	@Column(unique = true)
	private String username;

	@NotNull
	private String hashedPassword;

	@OneToOne(cascade = { CascadeType.ALL })
	@NotNull
	private UserRoles userRoles;

	private String avatarPath;
	
	@NotNull
	@Column(name = "createdAt")
	private Timestamp createdAt;

	@NotNull
	@Min(0)
	private Integer notifications = 0;
	
	@ElementCollection
	@JsonIgnore
	private List<Long> likes = new ArrayList<>();

	@SuppressWarnings("unused")
	private User() {

	}
	
	public User(String username, String hashedPassword, UserRoles userRoles, Timestamp createdAt) {
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.userRoles = userRoles;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public Integer getNotifications() {
		return notifications;
	}

	public void setNotifications(Integer notifications) {
		this.notifications = notifications;
	}
	
	public void addNotification() {
		notifications++;
	}
	
	public void removeNotification() {
		notifications--;
		if(notifications < 0) {
			notifications = 0;
		}
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public List<Long> getLikes() {
		return likes;
	}

	public void setLikes(List<Long> likes) {
		this.likes = likes;
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
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
