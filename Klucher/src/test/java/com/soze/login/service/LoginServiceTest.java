package com.soze.login.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@Transactional
@ActiveProfiles("test")
@WebIntegrationTest
public class LoginServiceTest extends TestWithUserBase {

  @Autowired
  private LoginService loginService;

  @Test
  public void testUserDoesNotExist() {
    boolean loginSuccessful = loginService.manualLogin("invalid username",
        "invalid password", new MockHttpServletRequest());
    assertFalse(loginSuccessful);
  }

  @Test
  public void testUserExists() {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    boolean loginSuccessful = loginService.manualLogin(username, password,
        new MockHttpServletRequest());
    assertTrue(loginSuccessful);
  }

  @Test
  public void testUserExistsPasswordInvalid() {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    boolean loginSuccessful = loginService.manualLogin(username,
        "wrong password", new MockHttpServletRequest());
    assertFalse(loginSuccessful);
  }

  @Test
  public void testUserAlreadyLoggedIn() {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    boolean loginSuccessful = loginService.manualLogin(username, password,
        new MockHttpServletRequest());
    assertTrue(loginSuccessful);
    loginSuccessful = loginService.manualLogin(username, password,
        new MockHttpServletRequest());
    assertTrue(loginSuccessful);
  }

}
