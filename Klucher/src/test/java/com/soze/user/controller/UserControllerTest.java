package com.soze.user.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.soze.user.model.User;
import com.soze.user.service.UserFeedService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserControllerTest extends TestWithMockUsers {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mvc;
	
	@MockBean
	private UserFeedService userFeedService;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@Test
	public void getProfileUserDoesNotExist() throws Exception {
		String username = "user";
		mvc.perform(MockMvcRequestBuilders.get("/u/profile/" + username))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void getProfileUserExistChecksOwnProfileIsLoggedIn() throws Exception {
		String username = "user";
		mockUser(username, true);
		mvc.perform(MockMvcRequestBuilders.get("/u/profile/" + username))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/dashboard"));
	}
	
	@Test
	public void getProfileUserExistsNotOwnProfileLoggedIn() throws Exception {
		String username = "user";
		mockUser(username, true);
		String anotherUsername = "anotherUser";
		User anotherUser = mockUser(anotherUsername, false);
		mvc.perform(MockMvcRequestBuilders.get("/u/profile/" + anotherUsername))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("user"))
			.andExpect(model().attribute("follows", equalTo(false)))
			.andExpect(model().attribute("username", equalTo(anotherUsername)))
			.andExpect(model().attribute("avatarPath", equalTo(anotherUser.getAvatarPath())))
			.andExpect(model().attribute("loggedIn", equalTo(true)));
	}
	
	@Test
	public void getProfileUserExistsNotLoggedIn() throws Exception {
		String username = "username";
		User user = mockUser(username, false);
		mvc.perform(MockMvcRequestBuilders.get("/u/profile/" + username))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("user"))
			.andExpect(model().attributeDoesNotExist("follows"))
			.andExpect(model().attribute("username", equalTo(username)))
			.andExpect(model().attribute("avatarPath", equalTo(user.getAvatarPath())))
			.andExpect(model().attribute("loggedIn", equalTo(false)));
	}
	
	@Test
	public void getFollowersFeed() throws Exception {
		String username = "user";
		mockUser(username);
		mvc.perform(MockMvcRequestBuilders.get("/u/followers/" + username)
				.param("next", "" + Long.MAX_VALUE))
		.andDo(print())
		.andExpect(status().isOk());
		verify(userFeedService).getFollowerFeed(username, new FeedRequest(FeedDirection.NEXT, Long.MAX_VALUE));
	}
	

}
