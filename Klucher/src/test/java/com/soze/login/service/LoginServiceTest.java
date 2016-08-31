package com.soze.login.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.CannotLoginException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LoginServiceTest extends TestWithMockUsers {

  @Autowired
  private LoginService loginService;

  @Test(expected = CannotLoginException.class)
  public void testUserDoesNotExist() throws Exception {
    loginService.manualLogin("invalid username", "invalid password", new MockHttpServletRequest());
  }

  @Test
  public void testUserExists() throws Exception {
    String username = "username";
    String password = "password";
    mockUser(username, password);
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
  }

  @Test(expected = CannotLoginException.class)
  public void testUserExistsPasswordInvalid() throws Exception {
    String username = "username";
    String password = "password";
    mockUser(username, password);
    loginService.manualLogin(username,
        "wrong password", new MockHttpServletRequest());
  }

  @Test
  public void testUserAlreadyLoggedIn() throws Exception {
    String username = "username";
    String password = "password";
    mockUser(username, password);
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
    loginService.manualLogin(username, password,
        new MockHttpServletRequest());
  }

}
