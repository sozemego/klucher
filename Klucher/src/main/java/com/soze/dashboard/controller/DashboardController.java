package com.soze.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Controller
public class DashboardController {

  private final UserDao userDao;

  @Autowired
  public DashboardController(UserDao userDao) {
    this.userDao = userDao;
  }

  @RequestMapping("/dashboard")
  public String getDashboard(Authentication authentication, Model model) {
    String username = authentication.getName();
    User user = userDao.findOne(username);
    model.addAttribute("username", user.getUsername());
    model.addAttribute("avatarPath", user.getAvatarPath());
    return "dashboard";
  }

}
