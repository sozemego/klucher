package com.soze.ratelimiter.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import com.soze.ratelimiter.model.InteractionResult;
import com.soze.utils.FileUtils;

public class RateLimiterTest {

  private static final int DEFAULT_LIMIT = 60;
	private RateLimiter rateLimiter;
  
  @Before
  public void setUp() throws Exception {
    FileUtils utils = mock(FileUtils.class);
    when(utils.readLinesFromClasspathFile(anyString())).
      thenReturn(Arrays.asList(
      		"/register, post, 10",
      		"/register, get, 30",
      		"/login, post, 5"));
    rateLimiter = new RateLimiter(utils);
    rateLimiter.init();
  }
  
  @Test
  public void testOneInteraction() {
    String username = "user";
    InteractionResult result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(9));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testOneInteractionDefaultLimit() {
    String username = "user";
    InteractionResult result = rateLimiter.interact(username, "/abc", HttpMethod.GET);
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/abc"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.GET));
    assertThat(result.getLimit(), equalTo(DEFAULT_LIMIT));
    assertThat(result.getRemaining(), equalTo(59));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testMoreInteractions() {
    String username = "user";
    int timesToInteract = 9;
    InteractionResult result = null;
    for(int i = 0; i < timesToInteract; i++) {
      result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(1));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testTooManyInteractions() {
    String username = "user";
    int timesToInteract = 15;
    InteractionResult result = null;
    for(int i = 0; i < timesToInteract; i++) {
      result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(0));
    assertThat(result.getSecondsUntilInteraction(), greaterThan(0));
  }
  
  @Test
  public void testSameEndpointDifferentMethods() {
    String username = "user";
    int timesToInteract = 5;
    for(int i = 0; i < timesToInteract; i++) {
      rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    InteractionResult result = rateLimiter.interact(username, "/register", HttpMethod.GET);;
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.GET));
    assertThat(result.getLimit(), equalTo(30));
    assertThat(result.getRemaining(), equalTo(29));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void twoDifferentUsersOneInteraction() {
    String username = "user";
    InteractionResult result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(9));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
    String anotherUsername = "another";
    InteractionResult anotherResult = rateLimiter.interact(anotherUsername, "/register", HttpMethod.POST);
    assertThat(anotherResult.getInteraction().getUsername(), equalTo(anotherUsername));
    assertThat(anotherResult.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(anotherResult.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(anotherResult.getLimit(), equalTo(10));
    assertThat(anotherResult.getRemaining(), equalTo(9));
    assertThat(anotherResult.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testSameEndpointDifferentMethodsOverLimit() {
    String username = "user";
    int timesToInteract = 5;
    for(int i = 0; i < timesToInteract; i++) {
      rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    InteractionResult result = rateLimiter.interact(username, "/register", HttpMethod.PUT);;
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.PUT));
    assertThat(result.getLimit(), equalTo(DEFAULT_LIMIT));
    assertThat(result.getRemaining(), equalTo(59));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testSameEndpointDifferentMethodBothInFile() {
  	String username = "user";
    int timesToInteract = 5;
    InteractionResult result = null;
    for(int i = 0; i < timesToInteract; i++) {
    	result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(5));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
    
    for(int i = 0; i < timesToInteract; i++) {
    	result = rateLimiter.interact(username, "/register", HttpMethod.GET);
    }
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.GET));
    assertThat(result.getLimit(), equalTo(30));
    assertThat(result.getRemaining(), equalTo(25));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
    
  }
  
  @Test
  @Ignore
  public void testTimePassed() {
    String username = "user";
    int timesToInteract = 15;
    InteractionResult result = null;
    for(int i = 0; i < timesToInteract; i++) {
      result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    }
    try {
      Thread.sleep((60 * 1000) + (10 * 1000));
    } catch (Exception e) {
      fail("Waiting failed");
    }
    result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(9));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }
  
  @Test
  public void testNullUser() {
    String username = null;
    InteractionResult result = rateLimiter.interact(username, "/register", HttpMethod.POST);
    assertThat(result.getInteraction().getUsername(), equalTo(username));
    assertThat(result.getInteraction().getEndpoint(), equalTo("/register"));
    assertThat(result.getInteraction().getMethod(), equalTo(HttpMethod.POST));
    assertThat(result.getLimit(), equalTo(10));
    assertThat(result.getRemaining(), equalTo(9));
    assertThat(result.getSecondsUntilInteraction(), equalTo(0));
  }

}
