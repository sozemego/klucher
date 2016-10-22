package com.soze.usersettings.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.soze.common.exceptions.NotLoggedInException;

@Controller
public class UserSettingsController {

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public String getUserSettings(Authentication authentication) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		
		return "settings";
	}
	
}
