package com.soze.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soze.follow.service.FollowService;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.service.UserService;

@Controller
public class DashboardController {

  private final UserDao userDao;
  private final FollowService followService;
  private final UserService userService;

  @Autowired
  public DashboardController(UserDao userDao, FollowService followService, UserService userService) {
    this.userDao = userDao;
    this.followService = followService;
    this.userService = userService;
  }

  @RequestMapping("/dashboard")
  public String getDashboard(Authentication authentication, Model model) {
    String username = authentication.getName();
    User user = userDao.findOne(username);
    model.addAttribute("loggedUsername", user.getUsername());
    model.addAttribute("avatarPath", user.getAvatarPath());
    model.addAttribute("createdAt", user.getCreatedAt());
    model.addAttribute("numberOfLikes", user.getLikes().size());
    model.addAttribute("numberOfFollowers", followService.getNumberOfFollowers(user.getId()));
    model.addAttribute("username", username);
    model.addAttribute("kluchs", userService.getNumberOfKluchs(username));
    return "dashboard";
  }

}
