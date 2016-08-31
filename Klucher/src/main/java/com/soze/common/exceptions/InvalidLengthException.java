package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class InvalidLengthException extends RuntimeException {
	
	private final String parameter;
	private final Adjective adjective;
	
	public InvalidLengthException(String parameter, Adjective adjective) {
		this.parameter = parameter;
		this.adjective = adjective;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	public String getAdjective() {
		return adjective.toString().toLowerCase();
	}
	
	public enum Adjective {
		SHORT, LONG;
	}
	
}
