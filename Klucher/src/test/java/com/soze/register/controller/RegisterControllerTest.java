package com.soze.register.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.soze.TestWithRealUsers;
import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserAlreadyExistsException;
import com.soze.user.dao.UserDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegisterControllerTest extends TestWithRealUsers {

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
    assertThat(userDao.count(), equalTo(0L));
    mvc.perform(MockMvcRequestBuilders.get("/register"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(view().name("register"));
  }
  
  @Test
  public void testEmptyFields() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    try {
    	mvc.perform(MockMvcRequestBuilders.post("/register")
    			.accept(MediaType.APPLICATION_FORM_URLENCODED)
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.param("username", generateString(0))
    			.param("password", generateString(0)))
    	.andDo(print())
    	.andExpect(status().is(400));
    } catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(NullOrEmptyException.class));
    }
  }
  
  @Test
  public void testShortFields() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    try {
    	mvc.perform(MockMvcRequestBuilders.post("/register")
    			.accept(MediaType.APPLICATION_FORM_URLENCODED)
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.param("username", generateString(3))
    			.param("password", generateString(3)))
    	.andDo(print())
    	.andExpect(status().is(400));
    } catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(InvalidLengthException.class));
    }
  }
  
  @Test
  public void testValidUsernameInvalidPassword() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    try {
    	mvc.perform(MockMvcRequestBuilders.post("/register")
    			.accept(MediaType.APPLICATION_FORM_URLENCODED)
        	.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.param("username", generateString(4))
        	.param("password", generateString(3)))
    	.andDo(print())
    	.andExpect(status().is(400));
    } catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(InvalidLengthException.class));
    }
  }
  
  @Test
  public void testInvalidUsernameValidPassword() throws Exception {
  	assertThat(userDao.count(), equalTo(0L));
  	try {
  		mvc.perform(MockMvcRequestBuilders.post("/register")
  				.accept(MediaType.APPLICATION_FORM_URLENCODED)
  				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
  				.param("username", generateString(0))
  				.param("password", generateString(6)))
  		.andDo(print())
  		.andExpect(status().is(400));
  	} catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(NullOrEmptyException.class));
    }
  }
  
  @Test
  public void testTooLongFields() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    try {
    	mvc.perform(MockMvcRequestBuilders.post("/register")
    			.accept(MediaType.APPLICATION_FORM_URLENCODED)
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.param("username", generateString(65))
    			.param("password", generateString(65)))
    	.andDo(print())
    	.andExpect(status().is(400));
    } catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(InvalidLengthException.class));
    }
  }
  
  @Test
  public void testWayTooLongFields() throws Exception {
  	try {
  		assertThat(userDao.count(), equalTo(0L));
  		mvc.perform(MockMvcRequestBuilders.post("/register")
  				.accept(MediaType.APPLICATION_FORM_URLENCODED)
  				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
  				.param("username", generateString(6500))
  				.param("password", generateString(6500)))
    	.andDo(print())
    	.andExpect(status().is(400));
  	} catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(InvalidLengthException.class));
    }
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
    .andExpect(redirectedUrl("/dashboard"));
    assertThat(userDao.count(), equalTo(1L));
  }
  
  @Test
  public void testUserAlreadyExists() throws Exception {
    assertThat(userDao.count(), equalTo(0L));
    String username = "username";
    String password = "password";
    addUser(username, password);
    assertThat(userDao.count(), equalTo(1L));
    try {
    	mvc.perform(MockMvcRequestBuilders.post("/register")
        	.accept(MediaType.APPLICATION_FORM_URLENCODED)
        	.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        	.param("username", username)
        	.param("password", password))
    	.andDo(print())
    	.andExpect(status().is(400));
  	} catch (NestedServletException e) {
      assertThat(e.getCause().getClass(), equalTo(UserAlreadyExistsException.class));
    }
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
