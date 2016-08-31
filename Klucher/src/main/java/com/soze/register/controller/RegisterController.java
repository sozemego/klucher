package com.soze.register.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.CannotLoginException;
import com.soze.login.service.LoginService;
import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterService;

@Controller
@RequestMapping("/register")
public class RegisterController {

  private static final Logger log = LoggerFactory
      .getLogger(RegisterController.class);

  private final RegisterService registerService;
  private final LoginService loginService;

  @Autowired
  public RegisterController(RegisterService registerService,
      LoginService loginService) {
    this.registerService = registerService;
    this.loginService = loginService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public String getRegister(Authentication authentication) {
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      //return "redirect:/dashboard";
    }
    return "register";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String postRegister(@ModelAttribute("form") RegisterForm form,
      HttpServletRequest request) throws Exception {
    registerService.register(form);
    log.info("Registration of user [{}] was successful.", form.getUsername());
    login(form.getUsername(), form.getPassword(), request);
    return "redirect:/dashboard";
  }
  
  @RequestMapping(value = "/available/{name}")
  @ResponseBody
  public Boolean available(@PathVariable String name)  {
    log.info("A register controller which checks for available usernames was triggered for name [{}].", name);
    if(name == null || name.isEmpty()) {
      return false;
    }
    return registerService.isAvailable(name);
  }

  private void login(String username, String password, HttpServletRequest request)
      throws CannotLoginException {
    loginService.manualLogin(username, password, request);
  }

}
