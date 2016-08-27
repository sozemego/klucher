package com.soze.ratelimiter.service;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * A service which helps limit how many times in a given period of time
 * users can send requests to the server. However, this includes only a limited
 * functionality. E.g. limits how many Kluchs users can share or how many times they can 
 * send messages in chat rooms.
 * @author sozek
 *
 */
@Service
public class RateLimiter {

  private final static int REQUEST_LIMIT = 60;
  //time in seconds after made requests expire and stop counting towards limit
  private final static int REQUEST_TIME_PERIOD_IN_SECONDS = 60;
  private final Map<String, LinkedList<Long>> requests = new ConcurrentHashMap<>();
  
  /**
   * Method used to signal that a user made a request at given time (defined as
   * milliseconds since unix epoch).
   * 
   * @param username
   * @return true if given user is allowed to interact (made less requests per
   *         minute than allowed). false if username is null, empty or is valid
   *         but made too many requests
   */
  public HttpHeaders interact(String username) { 
    long currentTime = Instant.now().getEpochSecond();
    int requestsSince = requestsSince(username, currentTime);
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Rate-Limit-Limit", "" + REQUEST_LIMIT);
    int remaining = Math.max(0, (REQUEST_LIMIT - requestsSince) - 1);
    headers.add("X-Rate-Limit-Remaining", "" + remaining);
    long secondsUntilRequest = requestsSince < REQUEST_LIMIT ? 0L : secondsUntilRequest(username, currentTime);
    headers.add("X-Rate-Limit-Reset", "" + secondsUntilRequest);
    save(username, currentTime);
    return headers;
  }
  
  /**
   * Returns time in seconds until a given user will be able to 
   * make a request.
   * @param username
   * @param currentTime in seconds since UNIX epoch
   * @param timeInPast 
   * @return
   */
  private long secondsUntilRequest(String username, long currentTime) {
    LinkedList<Long> pastRequests = getPastRequests(username);
    //the list's size() should be larger than REQUEST_LIMIT at this point
    Long timestamp = pastRequests.get(pastRequests.size() - REQUEST_LIMIT);
    long differenceBetweenCurrentAndLimitTimestamp = currentTime - timestamp;
    return REQUEST_TIME_PERIOD_IN_SECONDS - differenceBetweenCurrentAndLimitTimestamp;
    
  }
  
  /**
   * Returns a number of requests made by this user in time period between current time
   * and however long in the past you want. E.g. pass current time in seconds since unix epoch
   * and a minute in seconds and this method will tell you how many requests were
   * successful in the last minute. Requests before timeInPast will be removed.
   * @param username
   * @param currentTime time of the request (seconds)
   * @param timeInPast how far in the past you want to go (in seconds)
   * @return
   */
  private int requestsSince(String username, long currentTime) {
    return purge(username, currentTime);
  }
  
  /**
   * Removes past requests outside of the given time period (current time in
   * seconds and time in seconds we wish to go back).
   * @param username
   * @param currentTime
   * @param timeInPast
   * @return
   */
  private int purge(String username, long currentTime) {
    long startPoint = currentTime - REQUEST_TIME_PERIOD_IN_SECONDS;
    long endPoint = currentTime;
    List<Long> pastRequests = getPastRequests(username);
    Iterator<Long> it = pastRequests.iterator();
    while (it.hasNext()) {
      Long next = it.next();
      if (next < startPoint || next > endPoint) {
        it.remove();
      }
    }
    return pastRequests.size();
  }
  
  private LinkedList<Long> getPastRequests(String username) {
    return createList(username);
  }
  
  private LinkedList<Long> createList(String username) {
    LinkedList<Long> list = requests.get(username);
    if(list == null) {
      list = new LinkedList<>();
      requests.put(username, list);
    }
    return list;
  }
  
  private void save(String username, long secondsCurrentTime) {
    List<Long> list = createList(username);
    list.add(secondsCurrentTime);
  }
  
}
