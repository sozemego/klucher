package com.soze.common.feed;

import java.util.Collection;

/**
 * Feed is an object which not only contains content but also information
 * neccessary to browse that content. It contains total number of elements, but
 * also fields (previous, next) needed to retrieve previous or next part of that
 * feed. If next is null, this feed has no more elements.
 * 
 * @author sozek
 *
 */
public class Feed<T> {

	private final Collection<T> elements;
	private final Long previous;
	private final Long next;

	public Feed(Collection<T> elements, Long previous, Long next) {
		this.elements = elements;
		this.previous = previous;
		this.next = next;
	}

	public Collection<T> getElements() {
		return elements;
	}

	public Long getPrevious() {
		return previous;
	}

	public Long getNext() {
		return next;
	}

	@Override
	public String toString() {
		return "Feed currently holds [" + elements.size() + "] elements."
				+ "next: [" + next + "]. Previous [" + previous + "].";
	}

}