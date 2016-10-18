package com.soze.user.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.follow.service.FollowService;
import com.soze.kluch.model.FeedRequest;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserFollowerView;
import com.soze.user.model.UserLikeView;
import com.soze.user.service.UserFeedService;
import com.soze.user.service.UserService;

@Controller
public class UserController {
  
  private final UserDao userDao;
  private final FollowService followService;
  private final UserFeedService userFeedService;
  private final UserService userService;
  
  @Autowired
  public UserController(UserDao userDao, FollowService followService, UserFeedService userFeedService, UserService userService) {
    this.userDao = userDao;
    this.followService = followService;
    this.userFeedService = userFeedService;
    this.userService = userService;
  }
  
  @RequestMapping(value = "/u/profile/{username}", method = RequestMethod.GET)
  public String userPage(Authentication authentication, @PathVariable String username, Model model) {
    User user = userDao.findOne(username);
    if(user == null) {
      return "redirect:/";
    }
    boolean loggedIn = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    if (loggedIn) {
      String authorizedUsername = authentication.getName();
      if(authorizedUsername.equals(username)) {
        return "redirect:/dashboard";
      }
      model.addAttribute("follows", followService.doesUsernameFollow(authorizedUsername, user));
      model.addAttribute("likes", userService.doesLike(authorizedUsername, username));
      model.addAttribute("loggedUsername", authorizedUsername);
    }
    model.addAttribute("username", username);
    model.addAttribute("loggedIn", loggedIn);
    model.addAttribute("avatarPath", user.getAvatarPath());
    model.addAttribute("createdAt", user.getCreatedAt());
    model.addAttribute("numberOfLikes", user.getLikes().size());
    model.addAttribute("numberOfFollowers", followService.getNumberOfFollowers(user.getId()));
    model.addAttribute("kluchs", userService.getNumberOfKluchs(username));
    return "user";
  }
  
  @RequestMapping(value = "/u/followers/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Feed<UserFollowerView> getFollowers(@PathVariable String username,
  		@RequestParam(required = false) Long next) throws Exception {
  	FeedRequest feedRequest = createFeedRequest(null, next, username);
  	return userFeedService.getFollowerFeed(username, feedRequest);
  }
  
  @RequestMapping(value = "/u/likes/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Feed<UserLikeView> getLikes(@PathVariable String username,
  		@RequestParam(required = false) Long next) throws Exception {
  	FeedRequest feedRequest = createFeedRequest(null, next, username);
  	return userFeedService.getLikeFeed(username, feedRequest);
  }
  
  private FeedRequest createFeedRequest(Long previous, Long next, String sourceUsername) {
  	Optional<String> source = sourceUsername == null ? Optional.empty() : Optional.of(sourceUsername);
		if (previous != null) {
			return new FeedRequest(FeedDirection.PREVIOUS, previous, source);
		}
		return new FeedRequest(FeedDirection.NEXT, next, source);
	}
  
  @RequestMapping(value = "/u/like", method = RequestMethod.POST)
  @ResponseBody
  public int like(Authentication authentication, @RequestParam String username) throws Exception {
  	if(authentication == null) {
  		throw new NotLoggedInException();
  	}
  	String loggedUsername = authentication.getName();
  	int likes = userService.like(loggedUsername, username);
  	return likes;
  }
  
  @RequestMapping(value = "/u/unlike", method = RequestMethod.POST)
  @ResponseBody
  public int unlike(Authentication authentication, @RequestParam String username) throws Exception {
  	if(authentication == null) {
  		throw new NotLoggedInException();
  	}
  	String loggedUsername = authentication.getName();
  	int likes = userService.unlike(loggedUsername, username);
  	return likes;
  }
  
}
