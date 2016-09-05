package com.soze.follow.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.soze.TestWithMockUsers;
import com.soze.follow.service.FollowService;
import com.soze.notification.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FollowControllerTest extends TestWithMockUsers {

	@Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mvc;
  
  @MockBean
  private FollowService followService;
  
  @MockBean
  private NotificationService notificationService;

  @Before
  public void setUp() throws Exception {
  	MockitoAnnotations.initMocks(this);
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }
	
  @Test
  public void testUnauthorizedFollow() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.post("/user/follow")
  			.param("follow", "user"))
  	.andDo(print())
  	.andExpect(status().isUnauthorized());  	
  }
  
  @Test
  public void testUnauthorizedUnfollow() throws Exception {
  	mvc.perform(MockMvcRequestBuilders.post("/user/unfollow")
  			.param("follow", "user"))
  	.andDo(print())
  	.andExpect(status().isUnauthorized());  	
  }
  
  @Test
  public void testValidFollow() throws Exception {
  	mockUser("test", true);
  	mvc.perform(MockMvcRequestBuilders.post("/user/follow")
  			.param("follow", "test1"))
  	.andDo(print())
  	.andExpect(status().isOk());
  	verify(followService).follow("test", "test1");
  	verify(notificationService).addFollowNotification("test", "test1");
  }
  
  @Test
  public void testValidUnfollow() throws Exception {
  	mockUser("test", true);
  	mvc.perform(MockMvcRequestBuilders.post("/user/unfollow")
  			.param("follow", "test1"))
  	.andDo(print())
  	.andExpect(status().isOk());
  	verify(followService).unfollow("test", "test1");
  	verifyZeroInteractions(notificationService);
  }

}
