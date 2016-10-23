package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class InvalidUserSettingException extends RuntimeException {

	private final String invalidSetting;
	
	public InvalidUserSettingException(String invalidSetting) {
		this.invalidSetting = invalidSetting;
	}
	
	public String getInvalidSetting() {
		return invalidSetting;
	}
	
}
