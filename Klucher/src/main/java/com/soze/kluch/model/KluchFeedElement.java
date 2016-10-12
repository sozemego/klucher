package com.soze.kluch.model;

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
	private final boolean liked;
	private final int likes;

	public KluchFeedElement(Kluch kluch, KluchUserView user, boolean liked, int likes) {
		this.kluch = kluch;
		this.user = user;
		this.liked = liked;
		this.likes = likes;
	}

	public Kluch getKluch() {
		return kluch;
	}

	public KluchUserView getUser() {
		return user;
	}
	
	public boolean isLiked() {
		return liked;
	}
	
	public int getLikes() {
		return likes;
	}

}
