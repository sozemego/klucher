package com.soze.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.soze.ratelimiter.service.RateLimitInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private RateLimitInterceptor rateLimitInterceptor;
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
    	.addResourceHandler("/resources/**")
    	.addResourceLocations("/resources/").setCachePeriod(60 * 60 * 24);
  }
  
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("login");
  }
  
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitInterceptor);
  }

}
