package com.soze.kluch.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	private Long authorId;
	@NotNull
	private String text;
	@NotNull
	private Timestamp timestamp;

	@JsonIgnore
	@ElementCollection
	private Set<String> hashtags = new HashSet<>();

	@JsonIgnore
	@ElementCollection
	private List<String> mentions = new ArrayList<>();

	public Kluch() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
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

	public List<String> getMentions() {
		return mentions;
	}

	public void setMentions(List<String> mentions) {
		this.mentions = mentions;
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
