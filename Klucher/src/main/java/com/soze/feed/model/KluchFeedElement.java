package com.soze.feed.model;

import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchUserView;

/**
 * DTO that contains a {@link Kluch} and relevant (to that kluch) user data.
 * 
 * @author sozek
 *
 */
public class KluchFeedElement {

	private final Kluch kluch;
	private final KluchUserView user;

	public KluchFeedElement(Kluch kluch, KluchUserView user) {
		this.kluch = kluch;
		this.user = user;
	}

	public Kluch getKluch() {
		return kluch;
	}

	public KluchUserView getUser() {
		return user;
	}

}
