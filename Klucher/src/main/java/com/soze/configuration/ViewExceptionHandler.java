package com.soze.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles exceptions that are supposed to return a view (instead of a JSON error response).
 * @author kamil jurek
 *
 */
@Service
public class ViewExceptionHandler implements HandlerExceptionResolver, Ordered {

	private static final Map<Class<?>, String> EXCEPTION_MESSAGE_MAP = new HashMap<>();
	private static final String DEFAULT_MESSAGE = "Ops, something went wrong!";

	@PostConstruct
	public void init() {
		EXCEPTION_MESSAGE_MAP.put(HttpRequestMethodNotSupportedException.class, "Sorry, cannot do that.");
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("statusCode", "");
		mav.addObject("message", getMessage(ex));
		mav.setStatus(HttpStatus.valueOf(response.getStatus()));
		return mav;
	}

	private String getMessage(Exception e) {
		return EXCEPTION_MESSAGE_MAP.getOrDefault(e.getClass(), DEFAULT_MESSAGE);
	}

}
