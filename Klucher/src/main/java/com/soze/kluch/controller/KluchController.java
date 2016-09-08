package com.soze.kluch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.service.KluchService;
import com.soze.notification.service.NotificationService;

@Controller
public class KluchController {
  
  private final KluchService kluchService;
  private final NotificationService notificationService;
  
  @Autowired
  public KluchController(KluchService kluchService,
  		@Qualifier("NotificationServiceWithCache") NotificationService notificationService) {
    this.kluchService = kluchService;
    this.notificationService = notificationService;
  }

  @RequestMapping(value = "/kluch", method = RequestMethod.POST)
  @ResponseBody
  public Kluch postKluch(Authentication authentication, @RequestParam String kluchText) throws Exception {
    if(authentication == null) {
      throw new NotLoggedInException();
    }
    String username = authentication.getName();
    Kluch kluch = kluchService.post(username, kluchText);
    notificationService.processKluch(kluch);
    return kluch;
  }
  
}
