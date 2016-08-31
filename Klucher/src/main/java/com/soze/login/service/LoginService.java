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

import com.soze.common.exceptions.CannotLoginException;

@Service
public class LoginService {

  private static final Logger log = LoggerFactory.getLogger(LoginService.class);
  private final AuthenticationProvider authenticationProvider;
  
  @Autowired
  public LoginService(AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }
  
  /**
   * Attempts to login given user with given password.
   * @param username
   * @param password
   * @param request
   * @throws CannotLoginException if authentication fails
   */
  public void manualLogin(String username, String password, HttpServletRequest request) throws CannotLoginException {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    token.setDetails(new WebAuthenticationDetails(request));
    Authentication authentication = null;
    
    try {
      authentication = authenticationProvider.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (AuthenticationException e) {
      throw new CannotLoginException("Could not log in " + username, e);
    }
    
    log.info("Logging in with [username: {}]", authentication.getPrincipal());
  }
  
}
