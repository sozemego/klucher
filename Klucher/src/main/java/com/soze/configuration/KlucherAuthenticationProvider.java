package com.soze.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

public class KlucherAuthenticationProvider implements AuthenticationProvider {

  private final UserDao userDao;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public KlucherAuthenticationProvider(UserDao userDao,
      PasswordEncoder passwordEncoder) {
    this.userDao = userDao;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    User user = userDao.findOne(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    boolean sameHash = passwordEncoder.matches(password, user.getHashedPassword());
    if (!sameHash) {
      throw new BadCredentialsException(username);
    }
    if(user.isDeleted()) {
    	throw new BadCredentialsException(username);
    }
    List<GrantedAuthority> grantedAuths = new ArrayList<>();
    for (GrantedAuthority ga : user.getAuthorities()) {
      grantedAuths.add(ga);
    }
    Authentication auth = new UsernamePasswordAuthenticationToken(username,
        user.getHashedPassword(), grantedAuths);
    return auth;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
  
}
