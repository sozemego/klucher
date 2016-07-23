package com.soze.register.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.Klucher;
import com.soze.register.model.RegisterForm;
import com.soze.user.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
public class RegisterServiceTest {
  
  @Autowired
  private RegisterService registerService;
  
  @Autowired
  private UserDao userDao;
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(0));
    form.setPassword(generateString(0));
    registerService.register(form);   
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testShortFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(3));
    form.setPassword(generateString(5));
    registerService.register(form);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testValidUsernameInvalidPassword() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(4));
    form.setPassword(generateString(5));
    registerService.register(form);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUsernameValidPassword() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(0));
    form.setPassword(generateString(6));
    registerService.register(form);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testTooLongFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(65));
    form.setPassword(generateString(65));
    registerService.register(form);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testWayTooLongFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(65000));
    form.setPassword(generateString(65000));
    registerService.register(form);
  }
  
  @Test
  public void testValidFields() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(6));
    form.setPassword(generateString(6));
    assertThat(userDao.count(), equalTo(0l));
    registerService.register(form);
    assertThat(userDao.count(), equalTo(1l));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testUserAlreadyExists() {
    RegisterForm form = new RegisterForm();
    form.setUsername(generateString(4));
    form.setPassword(generateString(6));
    registerService.register(form);
    registerService.register(form);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullValues() {
    RegisterForm form = new RegisterForm();
    form.setUsername(null);
    form.setPassword(null);
    registerService.register(form);  
  }
  
  private String generateString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
      sb.append("c");
    }
    return sb.toString();
  }

}
