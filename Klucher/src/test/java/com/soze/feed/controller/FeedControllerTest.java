package com.soze.feed.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@WebIntegrationTest
@Transactional
@ActiveProfiles("test")
public class FeedControllerTest extends TestWithUserBase {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(springSecurity()).build();
  }

  @Test
  public void testUnauthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/feed")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("timestamp", "0"))
    .andDo(print())
    .andExpect(status().isUnauthorized());
  }
  
  @Test
  public void testAuthorizedButInvalidTimestampParameter() throws Exception {
    addUserToDbAndLogin("test", "password");
    mvc.perform(MockMvcRequestBuilders.get("/feed")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }
  
  /**
   * Only one test for this, since actual feed construction is
   * tested in FeedConstructorTest class.
   * @throws Exception
   */
  @Test
  public void testAuthorizedAndValidTimestamp() throws Exception {
    addUserToDbAndLogin("test", "password");
    mvc.perform(MockMvcRequestBuilders.get("/feed")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.kluchs.content", hasSize(0)))
    .andExpect(jsonPath("$.kluchs.last", equalTo(true)))
    .andExpect(jsonPath("$.kluchs.first", equalTo(true)))
    .andExpect(jsonPath("$.kluchs.size", equalTo(30)));
  }
  
  @Test
  public void testPollUnauthorized() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/feed/poll")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0"))
    .andDo(print())
    .andExpect(status().isUnauthorized());
  }
  
  @Test
  public void testPollAuthorizedButInvalidTimestampParameter() throws Exception {
    addUserToDbAndLogin("test", "password");
    mvc.perform(MockMvcRequestBuilders.get("/feed/poll")
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }
  
  /**
   * Only one test for this, since actual feed construction is
   * tested in FeedConstructorTest class.
   * @throws Exception
   */
  @Test
  public void testPollAuthorizedAndValidTimestamp() throws Exception {
    addUserToDbAndLogin("test", "password");
    mvc.perform(MockMvcRequestBuilders.get("/feed")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .param("timestamp", "0"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.kluchs.content", hasSize(0)))
    .andExpect(jsonPath("$.kluchs.last", equalTo(true)))
    .andExpect(jsonPath("$.kluchs.first", equalTo(true)))
    .andExpect(jsonPath("$.kluchs.size", equalTo(30)));
  }

}
