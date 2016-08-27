package com.soze.ratelimiter.service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.soze.ratelimiter.model.Interaction;
import com.soze.ratelimiter.model.InteractionResult;
import com.soze.ratelimiter.model.Limit;
import com.soze.utils.FileUtils;

/**
 * A service which helps limit how many times in a given period of time
 * users can send requests to the server. However, this includes only a limited
 * functionality. E.g. limits how many Kluchs users can share.
 * @author sozek
 *
 */
@Service
public class RateLimiter {

  private static final String CONFIG_PATH = "config/limits.txt"; 
  private static final int DEFAULT_REQUEST_LIMIT = 60;
  //time in seconds after made requests expire and stop counting towards limit
  private final static int REQUEST_TIME_PERIOD_IN_SECONDS = 60;
  private final Map<String, Limit> limits = new HashMap<>();
  private final Map<HttpMethod, Integer> defaultLimits = new HashMap<>();
  private final Map<Interaction, LinkedList<Long>> requests = new ConcurrentHashMap<>();
  private final FileUtils fileUtils;
  
  @Autowired
  public RateLimiter(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }
 
  /**
   * Method used to rate-limit users. A "window" of previous interactions
   * is 60 seconds wide. The returned object contains information
   * to tell users how many requests they are allowed to make, how many are remaining
   * and if they exceeded their limit, it tells them when (in seconds) will they
   * be able to interact again.
   * @param username
   * @param endpoint
   * @param method
   * @return
   */
  public InteractionResult interact(String username, String endpoint, HttpMethod method) {
    long currentTime = Instant.now().getEpochSecond();
    Interaction interaction = save(username, endpoint, method, currentTime);
    Limit limit = getLimit(endpoint, method);
    int requestsSince = requestsSince(interaction, currentTime);
    int requestLimit = limit.getLimit();
    int remaining = Math.max(0, (requestLimit - requestsSince));
    int secondsUntilRequest = requestsSince < requestLimit ? 0 : secondsUntilRequest(interaction, requestLimit, currentTime);
    return new InteractionResult(interaction, requestLimit, remaining, secondsUntilRequest);
  }
  
  private Interaction save(String username, String endpoint, HttpMethod method, long currentTime) {
    Interaction interaction = new Interaction(username, endpoint, method);
    LinkedList<Long> pastRequests = requests.get(interaction);
    if(pastRequests == null) {
      pastRequests = new LinkedList<>();
      requests.put(interaction, pastRequests);
    }
    pastRequests.add(currentTime);
    return interaction;
  }

  private Limit getLimit(String endpoint, HttpMethod method) {
    Limit limit = limits.get(endpoint);
    if(limit == null || !limit.getMethod().equals(method)) {
      limit = new Limit(method, defaultLimits.get(method));
      limits.put(endpoint, limit);
    }    
    return limit;
  }
  
  private int requestsSince(Interaction interaction, long currentTime) {
    return purge(interaction, currentTime);
  }
  
  private int purge(Interaction interaction, long currentTime) {
    LinkedList<Long> pastRequests = requests.get(interaction);
    Iterator<Long> it = pastRequests.iterator();
    long startPoint = currentTime - REQUEST_TIME_PERIOD_IN_SECONDS;
    long endPoint = currentTime;
    while(it.hasNext()) {
      Long pastRequest = it.next();
      if(pastRequest < startPoint || pastRequest > endPoint) {
        it.remove();
      }
    }
    return pastRequests.size();
  }
  
  private int secondsUntilRequest(Interaction interaction, int limit, long currentTime) {
    LinkedList<Long> pastRequests = requests.get(interaction);
    //the list's size() should be larger than limit at this point
    Long timestamp = pastRequests.get(pastRequests.size() - limit);
    long differenceBetweenCurrentAndLimitTimestamp = currentTime - timestamp;
    return (int) (REQUEST_TIME_PERIOD_IN_SECONDS - differenceBetweenCurrentAndLimitTimestamp); 
  }
  
  @PostConstruct
  public void init() {
    initDefaults();
    try {
      List<String> limits = fileUtils.readLinesFromClasspathFile(CONFIG_PATH);
      loadLimits(limits);
    } catch (IOException e) {
      // do nothing since we're going to use defaults if anything goes wrong
    }
  }
  
  private void initDefaults() {
    defaultLimits.put(HttpMethod.GET, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.POST, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.DELETE, DEFAULT_REQUEST_LIMIT);
  }
  
  private void loadLimits(List<String> limits) {
    for(String limit: limits) {
      loadLimit(limit);
    }
  }
  
  private void loadLimit(String limit) {
    String[] tokens = limit.split(",");
    String endpoint = tokens[0].trim();
    String method = tokens[1].trim();
    String perMinute = tokens[2].trim();
    HttpMethod httpMethod = HttpMethod.resolve(method.toUpperCase());
    Integer perMinuteInteger = Integer.parseInt(perMinute);
    Limit limitModel = new Limit(httpMethod, perMinuteInteger);
    limits.put(endpoint, limitModel);
  } 
  
}
