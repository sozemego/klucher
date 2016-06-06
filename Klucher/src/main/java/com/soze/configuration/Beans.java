package com.soze.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * Beans which don't fit in any particular place.
 * @author sozek
 *
 */
@Configuration
public class Beans {

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate object = new RestTemplate();
    object.setErrorHandler(new MyResponseErrorHandler());
    return object;
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
}
