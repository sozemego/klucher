package com.soze.ratelimiter.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	
	
  private static final String CONFIG_PATH = "config/limits_prod.txt";
  private static final String DEV_CONFIG_PATH = "config/limits_dev.txt";
  private static final int DEFAULT_REQUEST_LIMIT = 60;
  //time in seconds after made requests expire and stop counting towards limit
  private final static int REQUEST_TIME_PERIOD_IN_SECONDS = 60;
  
  @Value("${spring.profiles.active}")
  private String profile;
  private final Map<String, Limit> limits = new HashMap<>();
  private final Map<HttpMethod, Integer> defaultLimits = new HashMap<>();
  private final Map<Interaction, List<Long>> requests = new HashMap<>();
  private final FileUtils fileUtils;
  
  @Autowired
  public RateLimiter(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }
 
  /**
   * Method used to rate-limit users. A "window" of time for previous interactions
   * is 60 seconds wide. The returned object contains information
   * to tell users how many requests they are allowed to make, how many are remaining
   * and if they exceeded their limit, it tells them when (in seconds) will they
   * be able to interact again.
   * @param username <code>username</code> of user that is trying to interact. Does not have to be
   *        an actual username (can be IP)
   * @param endpoint endpoint (mapping) user is trying to interact with
   * @param method
   * @return the result
   */
  public InteractionResult interact(String username, String endpoint, HttpMethod method) {
  	
    long currentTime = Instant.now().getEpochSecond();
    Interaction interaction = save(username, endpoint, method, currentTime);
     
    int limit = getLimit(endpoint, method);
    int requestLimit = limit;
    int requestsSince = requestsSince(interaction, currentTime);
    int remaining = Math.max(0, (requestLimit - requestsSince));
    int secondsUntilRequest = secondsUntilRequest(interaction, requestLimit, currentTime);
    
    return new InteractionResult(interaction, requestLimit, remaining, secondsUntilRequest);
  }
  
  /**
   * Saves this interaction.
   * @param username <code>username</code> of user that is trying to interact. Does not have to be
   *        an actual username (can be IP)
   * @param endpoint endpoint (mapping) user is trying to interact with
   * @param method
   * @param currentTime time of the interaction
   * @return
   */
  private Interaction save(String username, String endpoint, HttpMethod method, long currentTime) {
    Interaction interaction = new Interaction(username, endpoint, method);
    synchronized(requests) {
	    List<Long> pastRequests = requests.get(interaction);
	    if(pastRequests == null) {
	      pastRequests = Collections.synchronizedList(new LinkedList<>());
	      requests.put(interaction, pastRequests);
	    }
    	pastRequests.add(currentTime);
    	return interaction;
    }
  }

  private int getLimit(String endpoint, HttpMethod method) {
  	Limit limit = limits.get(endpoint);
  	if(limit == null) {
  		limit = addLimit(endpoint, method, defaultLimits.get(method));
  	}
  	Integer intLimit = limit.getLimitFor(method);
  	if(intLimit == null) {
  		limit = addLimit(endpoint, method, defaultLimits.get(method));
  	}
  	return limit.getLimitFor(method);
  }
  
  private int requestsSince(Interaction interaction, long currentTime) {
    return purge(interaction, currentTime);
  }
  
  /**
   * Removes all requests outside of the accepted window
   * and returns the number of remaining interactions (that are within the window).
   * @param interaction
   * @param currentTime
   * @return
   */
  private int purge(Interaction interaction, long currentTime) {

    List<Long> pastRequests = requests.get(interaction);
    synchronized (pastRequests) {
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
  }
  
	/**
	 * Returns number of seconds till a given user can interact with given
	 * endpoint with given HttpMethod (encapsulated as Interaction object).
	 * Returns 0 if we can interact immediately.
	 * @param interaction
	 * @param limit
	 * @param currentTime
	 * @return
	 */
  private int secondsUntilRequest(Interaction interaction, int limit, long currentTime) {
  	
    List<Long> pastRequests = requests.get(interaction);
    // did not exceed the limit, so we can interact immidiately
    if(pastRequests.size() < limit) {
    	return 0;
    }
    
    // get timestamp of the interaction which if it
    // clears the number of interactions will fall under limit
    Long timestamp = pastRequests.get(pastRequests.size() - limit);
    // how many seconds already passed between this interaction and now
    long differenceBetweenCurrentAndLimitTimestamp = currentTime - timestamp;
    // calculate the time in seconds after which this interaction will be purged
    return (int) (REQUEST_TIME_PERIOD_IN_SECONDS - differenceBetweenCurrentAndLimitTimestamp);
  }
  
  @PostConstruct
  public void init() {
    initDefaults();
    String configPath = CONFIG_PATH;
    if("dev".equalsIgnoreCase(profile)) {
    	configPath = DEV_CONFIG_PATH;
    }
    try {
      List<String> limits = fileUtils.readLinesFromClasspathFile(configPath);
      loadLimits(limits);
    } catch (IOException e) {
      // do nothing since we're going to use defaults if anything goes wrong
    }
  }
  
  private void initDefaults() {
    defaultLimits.put(HttpMethod.GET, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.POST, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.DELETE, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.PUT, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.HEAD, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.TRACE, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.PATCH, DEFAULT_REQUEST_LIMIT);
    defaultLimits.put(HttpMethod.OPTIONS, DEFAULT_REQUEST_LIMIT);
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
    addLimit(endpoint, httpMethod, perMinuteInteger);
  } 
  
  private Limit addLimit(String endpoint, HttpMethod method, int limit) {
  	Limit limitModel = limits.get(endpoint);
  	if(limitModel == null) {
    	limitModel = new Limit(); 	
    }
    limitModel.addLimit(method, limit);
    limits.put(endpoint, limitModel);
    return limitModel;
  }
  
}
