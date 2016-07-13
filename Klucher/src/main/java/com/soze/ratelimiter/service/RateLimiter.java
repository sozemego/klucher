package com.soze.ratelimiter.service;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

  private final static int REQUESTS_PER_MINUTE = 30;
  private final static long MINUTE_SECONDS = 60;
  private final Map<String, List<Long>> pastRequests = new ConcurrentHashMap<>();
  
  /**
   * Method used to signal that a user made a request at given time (defined as
   * milliseconds since unix epoch).
   * 
   * @param username
   * @return true if given user is allowed to interact (made less requests per
   *         minute than allowed). false if username is null, empty or is valid
   *         but made too many requests
   */
  public boolean interact(String username) {
    if(username == null || username.isEmpty()) {
      return false;
    }  
    long currentTime = Instant.now().getEpochSecond();
    save(username, currentTime);
    int requestsSince = requestsSince(username, currentTime, MINUTE_SECONDS);
    if (requestsSince > REQUESTS_PER_MINUTE) {
      return false;
    }  
    return true;
  }
  
  /**
   * Returns a number of requests made by this user in time period between current time
   * and however long in the past you want. E.g. pass current time in seconds since unix epoch
   * and a minute in seconds and this method will tell you how many requests were
   * successful in the last minute.
   * @param username
   * @param currentTime time of the request (seconds 
   * @param timePastInSeconds how far in the past you want to go
   * @return
   */
  private int requestsSince(String username, long currentTime, long timeInPast) {
    return purge(username, currentTime, timeInPast);
  }
  
  /**
   * Removes past requests outside of the given time period (current time in
   * seconds and time in seconds we wish to go back).
   * @param username
   * @param currentTime
   * @param timeInPast
   * @return
   */
  private int purge(String username, long currentTime, long timeInPast) {
    long startPoint = currentTime - timeInPast;
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
  
  private List<Long> getPastRequests(String username) {
    return createList(username);
  }
  
  private List<Long> createList(String username) {
    List<Long> list = pastRequests.get(username);
    if(list == null) {
      list = new LinkedList<>();
      pastRequests.put(username, list);
    }
    return list;
  }
  
  private void save(String username, long millisCurrentTime) {
    List<Long> list = createList(username);
    list.add(millisCurrentTime);
  }
  
}
