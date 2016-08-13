package com.soze.ratelimiter.service;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.soze.common.exceptions.HttpException;

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
    HttpHeaders headers = rateLimiter.interact(username);
    String secondsUntilRequest = headers.get("X-Rate-Limit-Reset").get(0);
    int secondsUntilRequestInt = Integer.parseInt(secondsUntilRequest);
    response.setHeader("X-Rate-Limit-Limit", headers.get("X-Rate-Limit-Limit").get(0));
    response.setHeader("X-Rate-Limit-Remaining", headers.get("X-Rate-Limit-Remaining").get(0));
    response.setHeader("X-Rate-Limit-Reset", headers.get("X-Rate-Limit-Reset").get(0));
    if(secondsUntilRequestInt > 0) {
      throw new HttpException("Too many requests, please wait " + headers.get("X-Rate-Limit-Reset").get(0) + " seconds.", HttpStatus.TOO_MANY_REQUESTS);
    }   
    return super.preHandle(request, response, handler);
  }

}
