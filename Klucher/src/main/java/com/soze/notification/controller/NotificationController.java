package com.soze.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.feed.model.Feed;
import com.soze.notification.model.Notification;
import com.soze.notification.service.NotificationService;

@Controller
public class NotificationController {

	private final NotificationService notificationService;
	
	@Autowired
	public NotificationController(@Qualifier("NotificationServiceWithCache") NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@RequestMapping(value = "/notification/poll", method = RequestMethod.GET)
	@ResponseBody
	public Integer pollNotifications(Authentication authentication) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		return notificationService.poll(username);
	}
	
	@RequestMapping(value = "/notification", method = RequestMethod.GET)
	@ResponseBody
	public Feed<Notification> getNotifications(Authentication authentication) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		return notificationService.getNotifications(username);
	}
	
	@RequestMapping(value = "/notifications", method = RequestMethod.GET)
	public String getNotificationsPage(Authentication authentication, Model model) throws Exception {
		if(authentication == null) {
			return "redirect:/login";
		}
		model.addAttribute("username", authentication.getName());
		return "notifications";
	}
	
	@RequestMapping(value = "/notification/read", method = RequestMethod.POST)
	public ResponseEntity<Object> markNotificationsAsRead(Authentication authentication) throws Exception {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		notificationService.read(username);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
