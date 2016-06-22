package com.soze.dev.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
  
  @RequestMapping("/genKluchs/{username}")
  public void genKluchs(@PathVariable String username, @RequestParam(required = true) Integer number, @RequestParam(required = false) Integer milis) {
    log.info("Adding ");
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    FixedRatePoster poster = new FixedRatePoster(username, number, executor);
    executor.scheduleAtFixedRate(poster, 0, milis == null ? 250 : milis, TimeUnit.MILLISECONDS);
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
    
    FixedRatePoster(String username, int timesToRun, ScheduledExecutorService executor) {
      this.username = username;
      this.timesToRun = timesToRun;
      this.executor = executor;
    }
    
    public void run() {
      kluchService.post(username, kluchGenerator.getRandomKluch());
      timesRun++;
      if(timesRun >= timesToRun) {
        executor.shutdown();
      }
    }
  }
  
}
