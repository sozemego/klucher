package com.soze.hashtag.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class HashtagControllerTest extends TestWithMockUsers {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  public void testValidHashtag() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/dupa"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo("dupa")))
      .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void testAnotherHashtag() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/enemenemene"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo("enemenemene")))
      .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void testHashtagUppercase() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/dupA"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(model().attribute("hashtag", equalTo("dupa")))
    .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void testEmptyHashtag() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/"))
      .andDo(print())
      .andExpect(view().name("redirect:/dashboard"))
      .andExpect(status().is3xxRedirection());
  }
  
  @Test
  public void loggedIn() throws Exception {
    mockUser("test", "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/dupa"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo("dupa")))
      .andExpect(model().attribute("loggedIn", equalTo(true)));
  }
  
  @Test
  public void testEmptyFeed() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/feed/dupa")
        .param("timestamp", "0"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.kluchs.content", hasSize(0))) // we mostly care about page size here, not actual Kluchs
      .andExpect(jsonPath("$.kluchs.last", equalTo(true)))
      .andExpect(jsonPath("$.kluchs.first", equalTo(true)))
      .andExpect(jsonPath("$.kluchs.size", equalTo(30)));
  }
  
  @Test
  public void getFeedMissingTimestamp() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/feed/dupa"))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }
  
}
