package com.soze.common.exceptions;


@SuppressWarnings("serial")
public class NullOrEmptyException extends RuntimeException {

	private final String parameter;
	
	public NullOrEmptyException(String parameter) {
		this.parameter = parameter;
	}
	
	public String getParameter() {
		return parameter;
	}
	
}
