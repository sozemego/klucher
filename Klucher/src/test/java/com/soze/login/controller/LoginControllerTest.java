package com.soze.login.controller;

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

import com.soze.TestWithMockUsers;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LoginControllerTest extends TestWithMockUsers {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }
  
  @Test
  public void getPage() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/login")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("login"));
  }
  
  @Test
  public void alreadyLoggedInTest() throws Exception {
    mockUser("user", "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/login")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/dashboard"));
  }
  
  @Test
  public void testValidLogin() throws Exception {
    String username = "user";
    String password = "password";
    mockUser(username, password);
    mvc.perform(MockMvcRequestBuilders.post("/login")
        .param("username", username)
        .param("password", password)
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }
  
  @Test
  public void testWrongPasswordLogin() throws Exception {
    String username = "user";
    mockUser(username, "password");
    mvc.perform(MockMvcRequestBuilders.post("/login")
        .param("username", username)
        .param("password", "wrong password")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error"));
  }
  
  @Test
  public void testUserDoesNotExist() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/login")
        .param("username", "user")
        .param("password", "password")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error"));
  }

}
