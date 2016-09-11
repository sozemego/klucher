package com.soze.feed.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

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

import com.soze.TestWithMockUsers;
import com.soze.feed.service.FeedConstructor;
import com.soze.feed.service.FeedConstructor.FeedDirection;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FeedControllerTest extends TestWithMockUsers {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @MockBean
  private FeedConstructor feedConstructor;

  @Before
  public void setUp() throws Exception {
  	MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }

	@Test
	public void testUnauthorized() throws Exception {
		String username = "test";
		mvc.perform(MockMvcRequestBuilders.get("/feed/" + username)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.param("timestamp", "0"))
		.andDo(print())
		.andExpect(status().isOk());
		verify(feedConstructor).constructFeed(username, 0, true, FeedDirection.AFTER);
	}
  
  @Test
  public void testAuthorizedButInvalidTimestampParameter() throws Exception {
  	String username = "test";
    mockUser(username, "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/feed/" + username)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }
  
  @Test
  public void testAuthorizedAndValidTimestamp() throws Exception {
  	String username = "test";
    mockUser(username, "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/feed/" + username)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0"))
    .andDo(print())
    .andExpect(status().isOk());
    verify(feedConstructor).constructFeed(username, 0, false, FeedDirection.AFTER);
  }
  
  @Test
  public void testAuthorizedAndValidTimestampAndDirectionBefore() throws Exception {
  	String username = "test";
    mockUser(username, "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/feed/" + username)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0")
        .param("direction", "before"))
    .andDo(print())
    .andExpect(status().isOk());
    verify(feedConstructor).constructFeed(username, 0, false, FeedDirection.BEFORE);
  }
  
  @Test
  public void testPollAuthorizedButInvalidTimestampParameter() throws Exception {
    mockUser("test", "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/feed/poll")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }
  
  @Test
  public void testGetKluchsWithMentionsUnauthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/feed/notification")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("kluchIds[]", "1", "2", "3"))
    .andDo(print())
    .andExpect(status().is(401));
  }
  
  @Test
  public void testGetKluchsWithMentions() throws Exception {
  	mockUser("test", true);	
    mvc.perform(MockMvcRequestBuilders.get("/feed/notification")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("kluchIds[]", "1", "2", "3"))
    .andDo(print())
    .andExpect(status().is(200));
    verify(feedConstructor).getKluchs(Arrays.asList(1L, 2L, 3L));
  }
  
  @Test
  public void testEmptyFeed() throws Exception {
  	String hashtag = "dupa";
    mvc.perform(MockMvcRequestBuilders.get("/feed/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0"))
      .andDo(print())
      .andExpect(status().isOk());
    verify(feedConstructor).constructHashtagFeed(hashtag, 0L);
  }
  
  @Test
  public void getFeedMissingTimestamp() throws Exception {
  	String hashtag = "dupa";
    mvc.perform(MockMvcRequestBuilders.get("/feed/hashtag/" + hashtag))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }

}
