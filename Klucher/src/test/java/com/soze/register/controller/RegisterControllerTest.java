package com.soze.register.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.soze.TestWithRealUsers;
import com.soze.common.exceptions.UserAlreadyExistsException;
import com.soze.login.service.LoginService;
import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegisterControllerTest extends TestWithRealUsers {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @MockBean
  private RegisterService registerService;
  
  @MockBean
  private LoginService loginService;
  
  @Before
  public void setUp() throws Exception {
  	MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }
  
  @Test
  public void testGetRegister() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/register")
    		.accept(MediaType.APPLICATION_FORM_URLENCODED)
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(view().name("register"));
  }
  
  @Test
  public void testValidFields() throws Exception {
    String username = "user";
		String password = "password";
		mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("username", username)
        .param(password, password))
    .andDo(print())
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl("/dashboard"));
    verify(registerService).register(new RegisterForm(username, password));
    verify(loginService).manualLogin(eq(username), eq(password), any(HttpServletRequest.class));
  }
  
  @Test
  public void testDifferentValidFields() throws Exception {
    String username = "differentUser";
		String password = "differentPassword";
		mvc.perform(MockMvcRequestBuilders.post("/register")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("username", username)
        .param("password", password))
    .andDo(print())
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl("/dashboard"));
    verify(registerService).register(new RegisterForm(username, password));
    verify(loginService).manualLogin(eq(username), eq(password), any(HttpServletRequest.class));
  }
  
  @Test
  public void testUserAlreadyExists() throws Exception {
    String username = "username";
    String password = "password";
    when(registerService.register(new RegisterForm(username, password)))
    .thenThrow(new UserAlreadyExistsException(username));
    mvc.perform(MockMvcRequestBuilders.post("/register")
       	.accept(MediaType.APPLICATION_JSON)
       	.contentType(MediaType.APPLICATION_JSON)
       	.param("username", username)
       	.param("password", password))
    .andDo(print())
    .andExpect(status().is(400));
    verifyZeroInteractions(loginService);
  }
  
}
