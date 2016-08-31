package com.soze.common.exceptions;

import com.soze.ratelimiter.model.InteractionResult;

@SuppressWarnings("serial")
public class TooManyRequestsException extends RuntimeException {

	private final InteractionResult result;
	
	public TooManyRequestsException(InteractionResult result) {
		this.result = result;
	}
	
	public InteractionResult getResult() {
		return result;
	}
		
}
