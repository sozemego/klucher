package com.soze.notification.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.soze.kluch.model.Kluch;

/**
 * A notification which contains a {@link Kluch} ID in which a user was
 * mentioned (@username).
 * 
 * @author sozek
 *
 */
@Embeddable
public class MentionNotification extends Notification {

	@NotNull
	private Long kluchId;
	@NotNull
	private Long timestamp;
	@NotNull
	private boolean noticed;

	public MentionNotification() {

	}

	public MentionNotification(long kluchId) {
		this.kluchId = kluchId;
	}

	public MentionNotification(long kluchId, long timestamp) {
		this.kluchId = kluchId;
		this.timestamp = timestamp;
	}

	public Long getKluchId() {
		return kluchId;
	}

	public void setKluchId(Long kluchId) {
		this.kluchId = kluchId;
	}

	public boolean isNoticed() {
		return noticed;
	}

	public void setNoticed(boolean noticed) {
		this.noticed = noticed;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kluchId == null) ? 0 : kluchId.hashCode());
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
		MentionNotification other = (MentionNotification) obj;
		if (kluchId == null) {
			if (other.kluchId != null)
				return false;
		} else if (!kluchId.equals(other.kluchId))
			return false;
		return true;
	}

}
