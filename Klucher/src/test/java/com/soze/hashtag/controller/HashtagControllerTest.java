package com.soze.hashtag.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
  	String hashtag = "dupa";
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_FORM_URLENCODED)
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo(hashtag)))
      .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void testAnotherHashtag() throws Exception {
  	String hashtag = "enemenemene";
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo(hashtag)))
      .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void testHashtagUppercase() throws Exception {
  	String hashtag = "dupA";
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(model().attribute("hashtag", equalTo(hashtag.toLowerCase())))
    .andExpect(model().attribute("loggedIn", equalTo(false)));
  }
  
  @Test
  public void loggedIn() throws Exception {
  	String hashtag = "dupa";
    mockUser("test", "password", true);
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/" + hashtag)
    		.accept(MediaType.APPLICATION_JSON)
    		.contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("hashtag", equalTo(hashtag)))
      .andExpect(model().attribute("loggedIn", equalTo(true)));
  }
  
  @Test
  public void testEmptyHashtag() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/hashtag/")
    		.accept(MediaType.APPLICATION_FORM_URLENCODED)
    		.contentType(MediaType.APPLICATION_FORM_URLENCODED))
      .andDo(print())
      .andExpect(view().name("redirect:/dashboard"))
      .andExpect(status().is3xxRedirection());
  }
  
  
}
