package com.soze.login.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private static final Logger log = LoggerFactory.getLogger(LoginService.class);
  private final AuthenticationProvider authenticationProvider;
  
  @Autowired
  public LoginService(AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }
  
  /**
   * This method accepts a user username and password, as well as a request
   * and attempts to authenticate given user. Returns true if log in succeeds, false otherwise.
   * @param username
   * @param password
   * @param request
   * @return true if user is successfully logged in, false otherwise
   */
  public boolean manualLogin(String username, String password, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    token.setDetails(new WebAuthenticationDetails(request));
    Authentication authentication = null;
    try {
      authentication = authenticationProvider.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (AuthenticationException e) {
      log.info("Could not login [username: {}].", username, e);
      return false;
    }
    log.info("Logging in with [username: {}]", authentication.getPrincipal());
    return true;
  }
  
}
