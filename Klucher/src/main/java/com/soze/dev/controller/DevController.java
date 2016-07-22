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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  public String genKluchsRandom(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    validateInput(number, millis, fastMode);
    log.info("Adding random [{}] posts for user [{}], fastMode [{}]", number, username, fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getRandomKluch);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getRandomKluch);
    }
    return "dev";
  }

  @RequestMapping("/dev/genKluchs/timestamp/{username}")
  public String genKluchsTimestamp(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    validateInput(number, millis, fastMode);
    log.info("Adding timestamp [{}] posts for user [{}], fastMode [{}]", number, username,
        fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getCurrentTimestamp);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getCurrentTimestamp);
    }
    return "dev";
  }

  @RequestMapping("/dev/genKluchs/id/{username}")
  public String genKluchsId(@PathVariable String username,
      @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode) {
    validateInput(number, millis, fastMode);
    log.info("Adding timestamp [{}] posts for user [{}], fastMode [{}]", number, username,
        fastMode);
    if(fastMode) {
      postFastMode(username, number, kluchGenerator::getUniqueIdAsText);
    } else {
      postFixedRate(username, number, millis, kluchGenerator::getUniqueIdAsText);
    }
    return "dev";
  }
  
  @RequestMapping(value = "dev/post", method = RequestMethod.POST)
  public String postDev(@RequestParam(required = true) String username,
      @RequestParam(required = true) Integer number,
      @RequestParam(defaultValue = "250") Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode,
      @RequestParam(defaultValue = "id") String mode) {
    validateInput(number, millis, fastMode);
    post(username, number, millis, fastMode, mode);
    return "dev";
  }
  
  @RequestMapping(value = "/dev", method = RequestMethod.GET)
  public String dev() {
    return "dev";
  }
  
  private void validateInput(int number, int millis, boolean fastMode) {
    if(number < 0) {
      throw new IllegalArgumentException("Number of Kluchs to post cannot be negative.");
    }
    if(millis < 0 && !fastMode) {
      throw new IllegalArgumentException("Milliseconds per post cannot be negative.");
    }
  }
  
  private void post(String username, int number, int millis, boolean fastMode, String mode) {
    if("id".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getUniqueIdAsText);
    }
    if("timestamp".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getCurrentTimestamp);
    }
    if("random".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getRandomKluch);
    }
  }
  
  private void post(String username, int number, int millis, boolean fastMode, Supplier<String> supplier) {
    if(fastMode) {
      postFastMode(username, number, supplier);
    } else {
      postFixedRate(username, number, millis, supplier);
    }
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  public String handleException(IllegalArgumentException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "dev";
  }
  
  private void postFixedRate(String username, Integer number, Integer millis, Supplier<String> supplier) {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(getNumberOfThreads(number));
    Runnable poster = new FixedRatePoster(username, number, executor,
        supplier);
    executor.scheduleAtFixedRate(poster, 0, getMillisBetweenPosts(millis, false),
        TimeUnit.MILLISECONDS);
  }
  
  private void postFastMode(String username, Integer number, Supplier<String> supplier) {
    int threads = getNumberOfThreads(number);
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    int runnables = threads;
    int numberPerRunnable = number / threads;
    for(int i = 0; i < runnables; i++) {     
      Runnable poster = new Poster(username, numberPerRunnable, supplier);
      executor.execute(poster);
    }  
    int remainder = number % threads;
    if(remainder > 0) {
      Runnable poster = new Poster(username, remainder, supplier);
      executor.execute(poster);
    }
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
  
  
  @RequestMapping(value = "/dev/delete", method = RequestMethod.DELETE)
  public void deleteKluchs(@RequestParam(required = true) String username) {
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
    private final Supplier<String> supplier;
    
    Poster(String username, int timesToRun, Supplier<String> supplier) {
      this.username = username;
      this.timesToRun = timesToRun;
      this.supplier = supplier;
    }
    
    public void run() {
      while(timesRun < timesToRun) {
        kluchService.post(username, supplier.get());
        timesRun++;       
      }
    }   
  }
  
}
