package com.soze.about.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AboutController {

	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String about(Authentication authentication, Model model) {
		boolean loggedIn = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("loggedIn", loggedIn);
		return "about";
	}
	 
	
}
