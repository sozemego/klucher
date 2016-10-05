package com.soze.register.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserAlreadyExistsException;
import com.soze.register.model.RegisterForm;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegisterServiceTest {
  
  @Autowired
  private RegisterService registerService;
  
  @MockBean
  private UserDao userDao;
  
  @Test(expected = NullOrEmptyException.class)
  public void testEmptyFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(0));
    form.setPassword(generateString(0));
    registerService.register(form);   
  }
  
  @Test(expected = InvalidLengthException.class)
  public void testValidUsernameInvalidPassword() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(4));
    form.setPassword(generateString(5));
    registerService.register(form);
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testInvalidUsernameValidPassword() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(0));
    form.setPassword(generateString(6));
    registerService.register(form);
  }
  
  @Test(expected = InvalidLengthException.class)
  public void testTooLongFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(65));
    form.setPassword(generateString(65));
    registerService.register(form);
  }
  
  @Test(expected = InvalidLengthException.class)
  public void testWayTooLongFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(65000));
    form.setPassword(generateString(65000));
    registerService.register(form);
  }
  
  @Test
  public void testValidFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername("user");
    form.setPassword("password");
    User user = getUser(form);
    when(userDao.save(user)).thenReturn(user);
    User registeredUser = registerService.register(form);
    assertThat(registeredUser, notNullValue());
    assertThat(registeredUser.getUsername(), equalTo("user"));
    assertThat(registeredUser.getPassword(), equalTo("password"));
  }
  
  @Test(expected = UserAlreadyExistsException.class)
  public void testUserAlreadyExists() {
    RegisterForm form = new RegisterForm();
    form.setUsername("user");
    form.setPassword(generateString(6));
    registerService.register(form);
    when(userDao.exists("user")).thenReturn(true);
    registerService.register(form);
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testNullValues() {
    RegisterForm form = new RegisterForm();
    form.setUsername(null);
    form.setPassword(null);
    registerService.register(form);  
  }
  
  private User getUser(RegisterForm form) {
    User user = new User(form.getUsername(), form.getPassword(), null);
    return user;
  }
  
  private String generateString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
      sb.append("c");
    }
    return sb.toString();
  }

}
