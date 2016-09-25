package com.soze.kluch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  		NotificationService notificationService) {
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
    notificationService.processUserMentions(kluch);
    return kluch;
  }
  
  @RequestMapping(value = "/kluch", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<Object> deleteKluch(Authentication authentication, @RequestParam Long kluchId) throws Exception {
  	if(authentication == null) {
      throw new NotLoggedInException();
    }
  	String username = authentication.getName();
  	Kluch kluch = kluchService.deleteKluch(username, kluchId);
  	notificationService.removeUserMentions(kluch);
  	return new ResponseEntity<Object>(HttpStatus.OK);
  }
  
}
