package com.soze.error.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MyErrorController implements ErrorController {
	
	private static final String PATH = "/error";
	private static final Map<Integer, String> CODE_MESSAGE_MAP = new HashMap<>();
	private static final String DEFAULT_MESSAGE = "Ops, something went wrong!";
	
	@PostConstruct
	public void init() {
		CODE_MESSAGE_MAP.put(404, "This page does not exist.");
	}

	@RequestMapping(value = PATH, method = RequestMethod.GET)
	public String renderError(HttpServletRequest request, HttpServletResponse response, Model model) {
		int statusCode = response.getStatus();
		model.addAttribute("statusCode", statusCode);
		model.addAttribute("message", getMessage(statusCode));
		return "error/error";
	}
	
	private String getMessage(int statusCode) {
		return CODE_MESSAGE_MAP.getOrDefault(statusCode, DEFAULT_MESSAGE);
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
	
}
