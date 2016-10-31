package com.soze.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  
  @Value("${cache.cacheperiod}")
  private Integer cachePeriod;
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
    	.addResourceHandler("/resources/**")
    	.addResourceLocations("/resources/").setCachePeriod(cachePeriod);
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
