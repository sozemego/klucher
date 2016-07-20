package com.soze.dev.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.dev.service.RandomKluchGenerator;
import com.soze.kluch.service.KluchService;

@Controller
@Profile("dev")
public class DevController {

  private static final Logger log = LoggerFactory.getLogger(DevController.class);
  private final KluchService kluchService;
  private final RandomKluchGenerator kluchGenerator;
  
  @Autowired
  public DevController(KluchService kluchService, RandomKluchGenerator kluchGenerator) {
    this.kluchService = kluchService;
    this.kluchGenerator = kluchGenerator;
  }
  
  @RequestMapping("/dev/genKluchs/random/{username}")
  public void genKluchsRandom(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    log.info("Adding random [{}] posts for user [{}], fastMode [{}]", number, username, fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getRandomKluch);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getRandomKluch);
    }
  }

  @RequestMapping("/dev/genKluchs/timestamp/{username}")
  public void genKluchsTimestamp(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    log.info("Adding timestamp [{}] posts for user [{}], fastMode [{}]", number, username,
        fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getCurrentTimestamp);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getCurrentTimestamp);
    }
  }

  @RequestMapping("/dev/genKluchs/id/{username}")
  public void genKluchsId(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    log.info("Adding timestamp [{}] posts for user [{}], fastMode [{}]", number, username,
        fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getUniqueIdAsText);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getUniqueIdAsText);
    }
  }
  
  private void postFixedRate(String username, Integer number, Integer millis, Supplier<String> supplier) {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(getNumberOfThreads(number));
    Runnable poster = new FixedRatePoster(username, number, executor,
        supplier);
    executor.scheduleAtFixedRate(poster, 0, getMillisBetweenPosts(millis, false),
        TimeUnit.MILLISECONDS);
  }
  
  private void postFastMode(String username, Integer number, Supplier<String> supplier) {
    ExecutorService executor = Executors.newFixedThreadPool(getNumberOfThreads(number));
    Runnable poster = new Poster(username, number, executor,
        supplier);
    executor.execute(poster);
  }
  
  private long getMillisBetweenPosts(Integer millis, boolean fastMode) {
    if(fastMode) return 1L;
    return millis == null ? 250L : millis;
  }
  
  private int getNumberOfThreads(Integer numberOfKluchs) {
    int threads = numberOfKluchs / 2500;
    if(threads > 6) threads = 6;
    if(threads == 0) threads = 1;
    return threads;
  }
  
  
  @RequestMapping("/dev/genKluchs/delete/{username}")
  public void deleteKluchs(@PathVariable String username) {
    log.info("Removing all posts for user [{}]", username);
    kluchService.deleteAll(username);
  }
  
  /**
   * A simple runnable which posts random Kluchs (specified amount of times) and then shuts the executor which contains it down.
   * @author sozek
   *
   */
  private class FixedRatePoster implements Runnable {
    
    private final String username;
    private final int timesToRun;
    private int timesRun;
    private final ExecutorService executor;
    private final Supplier<String> supplier;
    
    FixedRatePoster(String username, int timesToRun, ExecutorService executor, Supplier<String> supplier) {
      this.username = username;
      this.timesToRun = timesToRun;
      this.executor = executor;
      this.supplier = supplier;
    }
    
    public void run() {
      kluchService.post(username, supplier.get());
      timesRun++;
      if (timesRun >= timesToRun) {
        executor.shutdown();
      }
    }   
  }
  
  private class Poster implements Runnable {
    
    private final String username;
    private final int timesToRun;
    private int timesRun;
    private final ExecutorService executor;
    private final Supplier<String> supplier;
    
    Poster(String username, int timesToRun, ExecutorService executor, Supplier<String> supplier) {
      this.username = username;
      this.timesToRun = timesToRun;
      this.executor = executor;
      this.supplier = supplier;
    }
    
    public void run() {
      while(timesRun <= timesToRun) {
        kluchService.post(username, supplier.get());
        timesRun++;
        if (timesRun >= timesToRun) {
          executor.shutdown();
        }
      }
    }   
  }
  
}
