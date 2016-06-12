package com.soze.register.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
      return "user";
    }
    return "register";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String postRegister(@ModelAttribute("form") RegisterForm form,
      Model model, HttpServletRequest request) {
    Map<String, String> errors = new HashMap<>();
    registerService.register(form, errors);
    boolean registrationSuccessful = errors.isEmpty();
    log.info(
        "Registration of user [{}] was successful [{}]. Input validation contained [{}] errors. [{}]",
        form.getUsername(), registrationSuccessful, errors.size(), errors);
    if (registrationSuccessful) {
      login(form.getUsername(), form.getPassword(), request);
      return "redirect:user";
    }
    populateModelWithErrors(errors, model);
    addOldValuesBackToModel(form, model);
    return "register";
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

  private boolean login(String username, String password,
      HttpServletRequest request) {
    return loginService.manualLogin(username, password, request);
  }

  private void populateModelWithErrors(Map<String, String> errors,
      Model model) {
    model.addAllAttributes(errors);
  }

  private void addOldValuesBackToModel(RegisterForm form, Model model) {
    model.addAttribute("username", form.getUsername());
    model.addAttribute("password", form.getPassword());
  }

}
