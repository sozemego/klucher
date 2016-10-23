package com.soze.usersettings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.usersettings.model.UserSettings;
import com.soze.usersettings.service.UserSettingsService;

@Controller
public class UserSettingsController {
	
	private final UserSettingsService userSettingsService;
	
	@Autowired
	public UserSettingsController(UserSettingsService userSettingsService) {
		this.userSettingsService = userSettingsService;
	}

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public String getUserSettings(Authentication authentication, Model model) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		UserSettings userSettings = userSettingsService.getUserSettings(username);
		model.addAttribute("userSettings", userSettings);
		return "settings";
	}
	
	@RequestMapping(value = "/settings", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> saveUserSettings(Authentication authentication, @RequestBody UserSettings settings, Model model) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		userSettingsService.saveSettings(username, settings);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/settings/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Object> deleteUser(Authentication authentication) {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		userSettingsService.deleteUser(username);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
