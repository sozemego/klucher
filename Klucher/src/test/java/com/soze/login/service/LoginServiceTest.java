package com.soze.login.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.Klucher;
import com.soze.TestWithUserBase;
import com.soze.common.exceptions.HttpException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@Transactional
@ActiveProfiles("test")
@WebIntegrationTest
public class LoginServiceTest extends TestWithUserBase {

  @Autowired
  private LoginService loginService;

  @Test(expected = HttpException.class)
  public void testUserDoesNotExist() throws Exception {
    loginService.manualLogin("invalid username", "invalid password", new MockHttpServletRequest());
  }

  @Test
  public void testUserExists() throws Exception {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
  }

  @Test(expected = HttpException.class)
  public void testUserExistsPasswordInvalid() throws Exception {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    loginService.manualLogin(username,
        "wrong password", new MockHttpServletRequest());
  }

  @Test
  public void testUserAlreadyLoggedIn() throws Exception {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
  }

}
