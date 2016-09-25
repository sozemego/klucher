package com.soze.feed.model;

import java.util.Collection;

/**
 * Feed is an object which not only contains content but also information
 * neccessary to browse that content. It contains total number of elements, but
 * also fields (previous, next) needed to retrieve previous or next part of that
 * feed. If next is null, this feed has no more elements. Previous always signifies
 * the first element, even if you can't go back anymore.
 * 
 * @author sozek
 *
 */
public class Feed<T> {

	private final Collection<T> elements;
	private final Long previous;
	private final Long next;
	private final long totalElements;

	public Feed(Collection<T> elements, Long previous,
			Long next, long totalElements) {
		this.elements = elements;
		this.previous = previous;
		this.next = next;
		this.totalElements = totalElements;
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

	public long getTotalElements() {
		return totalElements;
	}

	@Override
	public String toString() {
		return "size: " + (elements == null ? 0 : elements.size());
	}

}
