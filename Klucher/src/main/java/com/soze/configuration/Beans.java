package com.soze.configuration;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
  
  @Bean(name = "kluchProcessingExecutor")
  public Executor getKluchProcessingExecutor() {
    ThreadPoolTaskExecutor kluchExecutor = new ThreadPoolTaskExecutor();
    kluchExecutor.setCorePoolSize(1);
    kluchExecutor.setMaxPoolSize(1);
    return kluchExecutor;
  }
  
}
