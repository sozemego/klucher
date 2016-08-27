package com.soze.kluch.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.soze.TestWithUserBase;
import com.soze.kluch.dao.KluchDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class KluchControllerTest extends TestWithUserBase {
  
  @Autowired
  private KluchDao kluchDao;
  
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
    mvc.perform(MockMvcRequestBuilders.post("/kluch"))
      .andDo(print())
      .andExpect(status().isBadRequest());  
  }
  
  @Test
  public void testUnauthorized() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", "text"))
    .andDo(print())
    .andExpect(status().isUnauthorized());
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  @Test
  public void testValidKluch() throws Exception {
    addUserToDbAndLogin("username", "password");
    assertThat(kluchDao.count(), equalTo(0L));  
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", "text"))
    .andDo(print())
    .andExpect(status().isOk());
    assertThat(kluchDao.count(), equalTo(1L));
  }
  
  @Test
  public void testAlreadyPosted() throws Exception {
    addUserToDbAndLogin("username", "password");
    assertThat(kluchDao.count(), equalTo(0L));
    String kluchText = generateString(50);
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText))
    .andDo(print())
    .andExpect(status().isOk());
    assertThat(kluchDao.count(), equalTo(1L));
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", kluchText))
    .andDo(print())
    .andExpect(status().isBadRequest());
    assertThat(kluchDao.count(), equalTo(1L));
  }
  
  @Test
  public void testEmptyText() throws Exception {
    addUserToDbAndLogin("username", "password");
    assertThat(kluchDao.count(), equalTo(0L));  
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", ""))
    .andDo(print())
    .andExpect(status().isBadRequest());
    assertThat(kluchDao.count(), equalTo(0L)); 
  }
  
  @Test
  public void testTooLongKluch() throws Exception {
    addUserToDbAndLogin("username", "password");
    assertThat(kluchDao.count(), equalTo(0L));  
    mvc.perform(MockMvcRequestBuilders.post("/kluch")
        .param("kluchText", generateString(251)))
    .andDo(print())
    .andExpect(status().isBadRequest());
    assertThat(kluchDao.count(), equalTo(0L)); 
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
