package com.soze.ratelimiter.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

/**
 * Represents integer limits to a number of HttpMethod's. The particular endpoint
 * is not stored here, but should be in a map (endpoint-limit key-value pair).
 * @author kamil jurek
 *
 */
public class Limit {

	private final Map<HttpMethod, Integer> limitMap = new HashMap<>();
	
	public Limit() {
		
	}
	
	public void addLimit(HttpMethod method, int limit) {
		this.limitMap.put(method, limit);
	}
	
	public Integer getLimitFor(HttpMethod method) {
		return limitMap.get(method);
	}
  
}
