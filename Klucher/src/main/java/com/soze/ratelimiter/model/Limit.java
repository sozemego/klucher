package com.soze.ratelimiter.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

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
