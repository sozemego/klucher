package com.soze.kluch.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.notification.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class KluchControllerTest extends TestWithMockUsers {
  
  @MockBean
  private KluchDao kluchDao;
  
  @MockBean
  private NotificationService notificationService;
  
  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
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
    verifyZeroInteractions(kluchDao);
    verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", "text")
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isUnauthorized());
    verifyZeroInteractions(kluchDao);
    verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testValidKluch() throws Exception {
    mockUser("username", "password", true); 
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", "text")
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk());
    verify(kluchDao).save(any(Kluch.class));
    verify(notificationService).processKluch(any(Kluch.class));
  }
  
  @Test
  public void testAlreadyPosted() throws Exception {
    mockUser("username", "password", true);
    String kluchText = generateString(50);
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk());
    verify(kluchDao).save(any(Kluch.class));
    verify(notificationService).processKluch(any(Kluch.class));
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
    verifyNoMoreInteractions(kluchDao);
    verifyNoMoreInteractions(notificationService);
  }
  
  @Test
  public void testEmptyText() throws Exception {
    mockUser("username", "password", true);
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", "")
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
    verifyZeroInteractions(kluchDao);
    verifyZeroInteractions(notificationService);
  }
  
  @Test
  public void testTooLongKluch() throws Exception {
    mockUser("username", "password", true);    
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", generateString(251))
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
    verifyZeroInteractions(kluchDao);
    verifyZeroInteractions(notificationService);
  }
  
  private String generateString(int length) {
    if(length == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
      sb.append("c");
    }
    return sb.toString();
  }

}
