package com.soze.kluch.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.soze.TestWithMockUsers;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.service.KluchService;
import com.soze.notification.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class KluchControllerTest extends TestWithMockUsers {
  
  @MockBean(name = "NotificationServiceWithCache")
  private NotificationService notificationService;
  
  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @MockBean
  private KluchService service;
  
  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }
  
  @Test
  public void testNullKluchText() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isBadRequest());
    verifyZeroInteractions(service);
    verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testUnauthorized() throws Exception {
  	String kluchText = "text";
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isUnauthorized());
    verifyZeroInteractions(service);
    verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testValidKluch() throws Exception {
  	String kluchText = "text";
    mockUser("username", "password", true); 
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk());
    verify(service).post("username", kluchText);
    verify(notificationService).processUserMentions(any(Kluch.class));
  }
  
  @Test
  public void testUnauthorizedDeleteKluch() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.delete("/kluch")
  			.param("kluchId", "0"))
  	.andDo(print())
  	.andExpect(status().isUnauthorized());
  	verifyZeroInteractions(service);
  	verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testAuthorizedDeleteKluch() throws Exception {
  	mockUser("username", true);
  	mvc.perform(MockMvcRequestBuilders.delete("/kluch")
  			.param("kluchId", "0"))
  	.andDo(print());
  	verify(service).deleteKluch("username", 0L);
  	verify(notificationService).removeUserMentions(any(Kluch.class));
  }

}
