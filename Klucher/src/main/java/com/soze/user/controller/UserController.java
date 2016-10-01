package com.soze.user.controller;

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

import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.follow.service.FollowService;
import com.soze.kluch.model.FeedRequest;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserFollowerView;
import com.soze.user.service.UserFeedService;

@Controller
public class UserController {
  
  private final UserDao userDao;
  private final FollowService followService;
  private final UserFeedService userFeedService;
  
  @Autowired
  public UserController(UserDao userDao, FollowService followService, UserFeedService userFeedService) {
    this.userDao = userDao;
    this.followService = followService;
    this.userFeedService = userFeedService;
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
    }
    model.addAttribute("username", username);
    model.addAttribute("loggedIn", loggedIn);
    model.addAttribute("avatarPath", user.getAvatarPath());
    return "user";
  }
  
  @RequestMapping(value = "/u/followers/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Feed<UserFollowerView> getFollowers(@PathVariable String username,
  		@RequestParam(required = false) Long next) throws Exception {
  	FeedRequest feedRequest = createFeedRequest(null, next);
  	return userFeedService.getFollowerFeed(username, feedRequest);
  }
  
  private FeedRequest createFeedRequest(Long previous, Long next) {
		if (previous != null) {
			return new FeedRequest(FeedDirection.PREVIOUS, previous);
		}
		return new FeedRequest(FeedDirection.NEXT, next);
	}
  
}
