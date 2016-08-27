package com.soze.ratelimiter.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.soze.common.exceptions.HttpException;
import com.soze.ratelimiter.model.InteractionResult;

@Service
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

  private final RateLimiter rateLimiter;

  @Autowired
  public RateLimitInterceptor(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    Principal principal = request.getUserPrincipal();
    String username = principal != null ? principal.getName() : request.getRemoteAddr();
    InteractionResult result = rateLimiter.interact(username, request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
    response.setHeader("X-Rate-Limit-Limit", "" + result.getLimit());
    response.setHeader("X-Rate-Limit-Remaining", "" + result.getRemaining());
    response.setHeader("X-Rate-Limit-Reset", "" + result.getSecondsUntilInteraction());
    if(result.getSecondsUntilInteraction() > 0) {
      throw new HttpException("Too many requests, please wait " + result.getSecondsUntilInteraction() + " seconds.", HttpStatus.TOO_MANY_REQUESTS);
    }   
    return super.preHandle(request, response, handler);
  }

}
