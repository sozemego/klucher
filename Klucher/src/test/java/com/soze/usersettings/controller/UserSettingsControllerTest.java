package com.soze.usersettings.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.TestWithMockUsers;
import com.soze.user.model.User;
import com.soze.usersettings.model.UserSettings;
import com.soze.usersettings.service.UserSettingsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserSettingsControllerTest extends TestWithMockUsers {

	@MockBean
	private UserSettingsService userSettingsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }
  
  @Test
  public void testGetSettingsUnauthorized() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.get("/settings"))
  		.andDo(print())
  		.andExpect(status().is3xxRedirection());
  	verifyZeroInteractions(userSettingsService);
  }
  
  @Test
  public void testGetSettingsAuthorized() throws Exception {
  	User user = mockUser("user", true);
  	when(userSettingsService.getUserSettings("user")).thenReturn(user.getUserSettings());
  	mvc.perform(MockMvcRequestBuilders.get("/settings"))
	  	.andDo(print())
	  	.andExpect(status().isOk())
	  	.andExpect(model().attributeExists("userSettings"));
  	verify(userSettingsService).getUserSettings("user");
  }
  
  @Test
  public void testSaveSettingsUnauthorized() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.post("/settings"))
	  	.andDo(print())
	  	.andExpect(status().is3xxRedirection());
  	verifyZeroInteractions(userSettingsService);
  }
  
  @Test
  public void testSaveSettingsAuthorizedInvalid() throws Exception {
  	mockUser("user", true);
  	mvc.perform(MockMvcRequestBuilders.post("/settings"))
	  	.andDo(print())
	  	.andExpect(status().isBadRequest());
  	verifyZeroInteractions(userSettingsService);
  }
  
  @Test
  public void testSaveSettingsAuthorizedValid() throws Exception {
  	User user = mockUser("user", true);
  	String jsonSettings = new ObjectMapper().writeValueAsString(user.getUserSettings());
  	when(userSettingsService.saveSettings(eq("user"), any(UserSettings.class))).thenReturn(user.getUserSettings());
  	mvc.perform(MockMvcRequestBuilders.post("/settings")
  			.contentType(MediaType.APPLICATION_JSON)
  			.content(jsonSettings))
	  	.andDo(print())
	  	.andExpect(status().isOk());
  	verify(userSettingsService).saveSettings(eq("user"), any(UserSettings.class));
  }
  
  
  
}
