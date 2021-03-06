package com.soze.notification.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.soze.notification.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class NotificationControllerTest extends TestWithMockUsers {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mvc;

	@MockBean
	private NotificationService notificationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity()).build();
	}

	@Test
	public void testNotLoggedIn() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/notification/poll")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(401));
		verifyZeroInteractions(notificationService);
	}

	@Test
	public void testNoNotificationsPoll() throws Exception {
		String username = "user";
		mockUser(username, "password", true);
		mvc.perform(MockMvcRequestBuilders.get("/notification/poll")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
		verify(notificationService).poll(username);
	}

	@Test
	public void testAFewUnreadNotifications() throws Exception {
		String username = "test";
		mockUser(username, "password", true);
		mvc.perform(MockMvcRequestBuilders.get("/notification/poll")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
		verify(notificationService).poll(username);
	}
	
	@Test
	public void testReadNotificationsUnauthorized() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/notification/read")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(401));
		verifyZeroInteractions(notificationService);
	}
	
	@Test
	public void testReadNotifications() throws Exception {
		String username = "test";
		mockUser(username, "password", true);
		mvc.perform(MockMvcRequestBuilders.post("/notification/read")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(200));
		verify(notificationService).read(username);
	}
	
	@Test
	public void testGetViewUnauthorized() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/notifications")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/login"));
	}
	
	@Test
	public void testGetView() throws Exception {
		String username = "test";
		mockUser(username, "password", true);
		mvc.perform(MockMvcRequestBuilders.get("/notifications")
				.accept(MediaType.APPLICATION_FORM_URLENCODED)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(view().name("notifications"));
	}

}
