package com.soze.register.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.soze.Klucher;
import com.soze.TestWithUserBase;
import com.soze.user.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@WebIntegrationTest
@ActiveProfiles("test")
@Transactional
public class RegisterControllerTest extends TestWithUserBase {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @Autowired
  private UserDao userDao;
  
  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }
  
  @Test
  public void testGetRegister() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/register"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(view().name("register"));
  }
  
  @Test
  public void testEmptyFields() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(0))
        .param("password", generateString(0)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("username_error"))
    .andExpect(model().attributeExists("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testShortFields() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(3))
        .param("password", generateString(3)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("username_error"))
    .andExpect(model().attributeExists("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testValidUsernameInvalidPassword() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(4))
        .param("password", generateString(3)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeDoesNotExist("username_error"))
    .andExpect(model().attributeExists("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testInvalidUsernameValidPassword() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(3))
        .param("password", generateString(6)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("username_error"))
    .andExpect(model().attributeDoesNotExist("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testTooLongFields() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(65))
        .param("password", generateString(65)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("username_error"))
    .andExpect(model().attributeExists("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testWayTooLongFields() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(65000))
        .param("password", generateString(65000)))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("username_error"))
    .andExpect(model().attributeExists("password_error"))
    .andExpect(model().attributeDoesNotExist("general"));
  }
  
  @Test
  public void testValidFields() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", generateString(4))
        .param("password", generateString(6)))
    .andDo(print())
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl("user"));
    assertThat(userDao.count(), equalTo(1L));
  }
  
  @Test
  public void testUserAlreadyExists() throws Exception {
    String username = "username";
    String password = "password";
    addUserToDb(username, password);
    assertThat(userDao.count(), equalTo(1L));
    mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", username)
        .param("password", password))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(model().attributeExists("general"));
    assertThat(userDao.count(), equalTo(1l));
  }
  
  private String generateString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
      sb.append("c");
    }
    return sb.toString();
  }
  
  

}
