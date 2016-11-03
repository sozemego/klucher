package com.soze.chat.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.common.exceptions.ChatRoomAlreadyOpenException;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ChatServiceTest {

	@Autowired
	private ChatService service;

	@Test(expected = ChatRoomAlreadyOpenException.class)
	public void testAddRoomAlreadyExists() throws Exception {
		String roomName = "testName";
		service.addChatRoom(roomName);
		service.addChatRoom(roomName);
	}

	public void testAddRoomDoesNotExist() throws Exception {
		String roomName = "testName1";
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(false));
		service.addChatRoom(roomName);
		exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(true));
	}

	@Test(expected = ChatRoomDoesNotExistException.class)
	public void testRemoveRoomDoesNotExist() throws Exception {
		String roomName = "testName2";
		service.removeChatRoom(roomName);
	}

	@Test
	public void testRemoveRoomExists() throws Exception {
		String roomName = "testName3";
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(false));
		service.addChatRoom(roomName);
		exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(true));
		service.removeChatRoom(roomName);
		exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(false));
	}

	@Test
	public void testDoesRoomExistExists() throws Exception {
		String roomName = "testName4";
		service.addChatRoom(roomName);
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(true));
	}

	@Test
	public void testDoesRoomExistDoesNotExist() throws Exception {
		String roomName = "testName5";
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(false));
	}
	
	@Test(expected = ChatRoomDoesNotExistException.class)
	public void testGetUsersRoomDoesNotExist() throws Exception {
		String roomName = "testName6";
		service.getUsers(roomName);
	}
	
	@Test
	public void testGetUsersRoomExists() throws Exception {
		String roomName = "testName7";
		service.addChatRoom(roomName);
		Set<String> usernames = service.getUsers(roomName);
		assertNotNull(usernames);
	}
	
	@Test
	public void testAddUserValid() throws Exception {
		String roomName = "testName8";
		String username = "two";
		String sessionId = "1";
		service.addChatRoom(roomName);
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(true));
		service.addUser(roomName, sessionId, username);
		Set<String> users = service.getUsers(roomName);
		assertThat(users.contains(username), equalTo(true));
	}

	@Test
	public void removeUserValid() throws Exception {
		String roomName = "testName9";
		String username = "two";
		String sessionId = "1";
		service.addChatRoom(roomName);
		boolean exists = service.isChatRoomOpen(roomName);
		assertThat(exists, equalTo(true));
		service.addUser(roomName, sessionId, username);
		assertThat(service.getUsers(roomName).contains(username), equalTo(true));
		service.removeUser(Optional.ofNullable(roomName), sessionId);
		assertThat(service.getUsers(roomName).contains(username), equalTo(false));
	}
	
}
