package com.soze.dev.controller;

import java.util.Random;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.dev.service.RandomKluchGenerator;
import com.soze.follow.service.FollowService;
import com.soze.kluch.service.KluchService;
import com.soze.notification.service.NotificationService;
import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterConverter;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.service.UserService;

@Controller
@Profile("dev")
public class DevController {

  private static final Logger log = LoggerFactory.getLogger(DevController.class);
  private final KluchService kluchService;
  private final RandomKluchGenerator kluchGenerator;
  private final FollowService followService;
  private final NotificationService notificationService;
  private final UserDao userDao;
  private final RegisterConverter registerConverter;
  private final UserService userService;
  
  @Autowired
  public DevController(KluchService kluchService, RandomKluchGenerator kluchGenerator,
  		FollowService followService, NotificationService notificationService,
  		UserDao userDao, RegisterConverter registerConverter, UserService userService) {
    this.kluchService = kluchService;
    this.kluchGenerator = kluchGenerator;
    this.followService = followService;
    this.notificationService = notificationService;
    this.userDao = userDao;
    this.registerConverter = registerConverter;
    this.userService = userService;
  }
  
  @RequestMapping(value = "dev/follow/list", method = RequestMethod.POST)
  public String followList(@RequestParam String username, @RequestParam(value = "followers[]") String[] followers) {
  	for(String name: followers) {
  		followService.follow(name, username);
  		notificationService.addNotification(username);
  	}
  	return "dev";
  }
  
  @RequestMapping(value = "dev/follow/create", method = RequestMethod.POST)
  public String followCreate(@RequestParam String username, @RequestParam int number) {	
  	for(int i = 0; i < number; i++) {
  		String name = createRandomUser();
  		followService.follow(name, username);
  		notificationService.addNotification(username);
  	}
  	log.info("Created [{}] users who will follow [{}].", number, username);
  	return "dev";
  }
  
  @RequestMapping(value = "dev/post", method = RequestMethod.POST)
  public String postDev(@RequestParam(required = true) String username,
      @RequestParam(required = true) Integer number,
      @RequestParam(defaultValue = "250") Integer millis,
      @RequestParam(defaultValue = "false") Boolean fastMode,
      @RequestParam(defaultValue = "id") String mode,
      @RequestParam(defaultValue = "false") Boolean deleteFirst) throws Exception {
    validateInput(number, millis, fastMode);
    if(deleteFirst) {
      //kluchService.deleteAll(username);
    }
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
    if("nohashtag".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getRandomKluch);
    }
    if("somehashtag".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getRandomKluchWithSomeHashtags);
    }
    if("allhashtag".equalsIgnoreCase(mode)) {
      post(username, number, millis, fastMode, kluchGenerator::getAllHashtagKluch);
    }
  }
  
  private void post(String username, int number, int millis, boolean fastMode, Supplier<String> supplier) {
    if(fastMode) {
      postFastMode(username, number, supplier);
    } else {
      postFixedRate(username, number, millis, supplier);
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
  public void deleteKluchs(@RequestParam(required = true) String username,
      @RequestParam(required = false) Integer id) {
    log.info("Removing all posts for user [{}]", username);
    if(id == null) {
      //kluchService.deleteAll(username);
    } else {
      //kluchService.deleteKluch(id);
    }
  }
  
  @RequestMapping(value = "/dev/kluch/like", method = RequestMethod.POST)
  public void likeKluch(@RequestParam Long kluchId, @RequestParam Integer number) {
  	for(int i = 0; i < number; i++) {
  		String username = createRandomUser();
  		kluchService.likeKluch(username, kluchId);
  	}
  }
  
  @RequestMapping(value = "/dev/user/like", method = RequestMethod.POST)
  public void likeUser(@RequestParam String username, @RequestParam Integer number) {
  	for(int i = 0; i < number; i++) {
  		String randomUsername = createRandomUser();
  		userService.like(randomUsername, username);
  	}
  }
  
  private String createRandomUser() {
  	String username = getRandomUserName();
  	if(!userDao.exists(username)) {
  		User user = registerConverter.convertRegisterForm(new RegisterForm(username, "password"));
  		userDao.save(user);
  	}
  	return username;
  }
  
  /**
   * A simple runnable which posts random Kluchs (specified amount of times) and then shuts the executor, which contains it, down.
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
      try {
        kluchService.post(username, supplier.get());
      } catch (IllegalArgumentException e) {
        //do nothing
      }
      timesRun++;
      if (timesRun >= timesToRun) {
        log.info("FixedRatePoster posted [{}] kluchs.", timesRun);
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
        try {
          kluchService.post(username, supplier.get());
        } catch (IllegalArgumentException e) {
          //do nothing
        }
        timesRun++;       
      }
      log.info("Poster posted [{}] kluchs.", timesRun);
    }   
  }
  
	private final Random random = new Random();
  
  private String getRandomUserName() {
  	String alphabet = "qwertyuiopasdfghjklzxcvbnm";
  	int length = random.nextInt((12 - 6) + 1) + 6; 	
  	StringBuilder sb = new StringBuilder(length);
  	for(int i = 0; i < length; i++) {
  		char randomChar = alphabet.charAt(random.nextInt(alphabet.length()));
  		sb.append(randomChar);
  	}
  	return sb.toString();
  }
  
}
