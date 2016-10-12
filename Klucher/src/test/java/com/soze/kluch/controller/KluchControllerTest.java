package com.soze.kluch.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
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
import com.soze.common.feed.FeedDirection;
import com.soze.kluch.model.FeedRequest;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.service.KluchFeedService;
import com.soze.kluch.service.KluchService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class KluchControllerTest extends TestWithMockUsers {
  
  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @MockBean
  private KluchService service;
  
  @MockBean
  private KluchFeedService kluchFeedService;
  
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
  }
  
  @Test
  public void testUnauthorizedPostKluch() throws Exception {
  	String kluchText = "text";
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isUnauthorized());
    verifyZeroInteractions(service);
  }
  
  @Test
  public void testValidKluch() throws Exception {
  	String kluchText = "text";
    mockUser("username", "password", true); 
    when(service.post("username", kluchText)).thenReturn(new Kluch(0, null, null));
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText)
        .accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk());   
    verify(service).post("username", kluchText);
  }
  
  @Test
  public void testUnauthorizedDeleteKluch() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.delete("/kluch")
  			.param("kluchId", "0"))
  	.andDo(print())
  	.andExpect(status().isUnauthorized());
  	verifyZeroInteractions(service);
  }
  
  @Test
  public void testAuthorizedDeleteKluch() throws Exception {
  	mockUser("username", true);
  	mvc.perform(MockMvcRequestBuilders.delete("/kluch")
  			.param("kluchId", "0"))
  	.andDo(print());
  	verify(service).deleteKluch("username", 0L);
  }
  
  @Test
	public void testUnauthorizedFeedAfter() throws Exception {
		String username = "test";
		mvc.perform(MockMvcRequestBuilders.get("/kluch/" + username)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
		verify(kluchFeedService).constructFeed(username, new FeedRequest(FeedDirection.NEXT, null), true);
	}
  
  @Test
	public void testUnauthorizedFeedBefore() throws Exception {
		String username = "test";
		mvc.perform(MockMvcRequestBuilders.get("/kluch/" + username)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
		verify(kluchFeedService).constructFeed(username, new FeedRequest(FeedDirection.NEXT, null), true);
	}
  
  @Test
	public void testFeedBothIds() throws Exception {
		String username = "test";
		mvc.perform(MockMvcRequestBuilders.get("/kluch/" + username)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.param("next", "0")
				.param("previous", "5"))
		.andDo(print())
		.andExpect(status().isOk());
		verify(kluchFeedService).constructFeed(username, new FeedRequest(FeedDirection.PREVIOUS, 5l), true);
	}
  
  @Test
  public void testAuthorizedAndValidId() throws Exception {
  	String username = "test";
    mockUser(username, "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/kluch/" + username)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("next", "5"))
    .andDo(print())
    .andExpect(status().isOk());
    verify(kluchFeedService).constructFeed(username, new FeedRequest(FeedDirection.NEXT, 5L), false);
  }
  
  @Test
  public void testGetKluchsWithMentionsUnauthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/kluch/mentions")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().is(401));
  }
  
  @Test
  public void testGetKluchsWithMentions() throws Exception {
  	mockUser("test", true);	
    mvc.perform(MockMvcRequestBuilders.get("/kluch/mentions")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("next", "0"))
    .andDo(print())
    .andExpect(status().is(200));
    verify(kluchFeedService).getMentions("test", new FeedRequest(FeedDirection.NEXT, 0L));
  }
  
  @Test
  public void testEmptyFeed() throws Exception {
  	String hashtag = "dupa";
    mvc.perform(MockMvcRequestBuilders.get("/kluch/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON)
        .param("next", "0"))
      .andDo(print())
      .andExpect(status().isOk());
    verify(kluchFeedService).constructHashtagFeed(null, hashtag, new FeedRequest(FeedDirection.NEXT, 0L));
  }

}
