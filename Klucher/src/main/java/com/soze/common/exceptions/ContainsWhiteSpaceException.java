package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class ContainsWhiteSpaceException extends RuntimeException {

	private final String parameter;
	
	public ContainsWhiteSpaceException(String parameter) {
		this.parameter = parameter;
	}
	
	public String getParameter() {
		return parameter;
	}
	
}
