package com.soze.dev.controller;

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
  
  @RequestMapping("/genKluchs/random/{username}")
  public void genKluchsRandom(@PathVariable String username, @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer milis) {
    log.info("Adding random [{}] posts for user [{}]", number, username);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    FixedRatePoster poster = new FixedRatePoster(username, number, executor, kluchGenerator::getRandomKluch);
    executor.scheduleAtFixedRate(poster, 0, milis == null ? 250 : milis, TimeUnit.MILLISECONDS);
  }
  
  @RequestMapping("/genKluchs/timestamp/{username}")
  public void genKluchsTimestamp(@PathVariable String username, @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer milis) {
    log.info("Adding timestamp [{}] posts for user [{}]", number, username);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    FixedRatePoster poster = new FixedRatePoster(username, number, executor, kluchGenerator::getCurrentTimestamp);
    executor.scheduleAtFixedRate(poster, 0, milis == null ? 0 : milis, TimeUnit.MILLISECONDS);
  }
  
  @RequestMapping("/genKluchs/id/{username}")
  public void genKluchsId(@PathVariable String username, @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer milis) {
    log.info("Adding timestamp [{}] posts for user [{}]", number, username);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    FixedRatePoster poster = new FixedRatePoster(username, number, executor, kluchGenerator::getUniqueIdAsText);
    executor.scheduleAtFixedRate(poster, 0, milis == null ? 0 : milis, TimeUnit.MILLISECONDS);
  }
  
  @RequestMapping("/genKluchs/delete/{username}")
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
    private final ScheduledExecutorService executor;
    private final Supplier<String> supplier;
    
    FixedRatePoster(String username, int timesToRun, ScheduledExecutorService executor, Supplier<String> supplier) {
      this.username = username;
      this.timesToRun = timesToRun;
      this.executor = executor;
      this.supplier = supplier;
    }
    
    public void run() {
      kluchService.post(username, supplier.get());
      timesRun++;
      if(timesRun >= timesToRun) {
        executor.shutdown();
      }
    }
  }
  
}
