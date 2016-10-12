package com.soze.kluch.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "kluchs")
public class Kluch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull
	private long authorId;
	@NotNull
	private String text;
	@NotNull
	private Timestamp timestamp;

	@JsonIgnore
	@ElementCollection
	private Set<String> hashtags = new HashSet<>();

	@JsonIgnore
	@ElementCollection
	private Set<String> mentions = new HashSet<>();
	
	@JsonIgnore
	@ElementCollection
	private Set<Long> likes = new HashSet<>();

	@SuppressWarnings("unused")
	private Kluch() {

	}
	
	public Kluch(long authorId, String text, Timestamp timestamp) {
		this.authorId = authorId;
		this.text = text;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setHashtags(Set<String> hashtags) {
		this.hashtags = hashtags;
	}

	public Set<String> getHashtags() {
		return hashtags;
	}

	public Set<String> getMentions() {
		return mentions;
	}

	public void setMentions(Set<String> mentions) {
		this.mentions = mentions;
	}

	public Set<Long> getLikes() {
		return likes;
	}

	public void setLikes(Set<Long> likes) {
		this.likes = likes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object two) {
		if (two == null) {
			return false;
		}
		if (this == two) {
			return true;
		}
		Kluch second = (Kluch) two;
		return getId() == second.getId();
	}

}
