package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.InvalidOwnerException;
import com.soze.common.exceptions.KluchDoesNotExistException;
import com.soze.common.exceptions.KluchPreviouslyPostedException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.model.Kluch;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class KluchServiceTest extends TestWithMockUsers {

	@Autowired
	@InjectMocks
	private KluchService kluchService;

	@Test(expected = InvalidLengthException.class)
	public void testTooLongKluch() throws Exception {
		kluchService.post("author", generateString(251));
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testValidKluchUserDoesNotExist() throws Exception {
		String kluchText = generateString(140);
		String author = "author";
		kluchService.post(author, kluchText);
	}
	

	@Test
	public void testValidKluch() throws Exception {
		String kluchText = generateString(140);
		String author = "author";
		User user = mockUser(author, false);
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthor().getId(), equalTo(user.getId()));
		assertThat(firstKluch.getText(), equalTo(kluchText));
	}

	@Test(expected = NullOrEmptyException.class)
	public void testEmptyKluch() throws Exception {
		kluchService.post("author", "");
	}

	@Test(expected = NullOrEmptyException.class)
	public void testNullKluch() throws Exception {
		kluchService.post("author", null);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testNullAuthor() throws Exception {
		kluchService.post(null, generateString(50));
	}

	@Test(expected = NullOrEmptyException.class)
	public void testEmptyAuthor() throws Exception {
		kluchService.post("", generateString(50));
	}

	@Test(expected = KluchPreviouslyPostedException.class)
	public void testAlreadyPosted() throws Exception {
		mockUser("author");
		String kluchText = generateString(50);
		kluchService.post("author", kluchText);
		kluchService.post("author", kluchText);
	}

	@Test
	public void testPostDifferentContent() throws Exception {
		String kluchText = generateString(50);
		String author = "author";
		User user = mockUser(author);		
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthor().getId(), equalTo(user.getId()));
		assertThat(firstKluch.getText(), equalTo(kluchText));
		String secondKluchText = generateString(51);
		Kluch anotherKluch = kluchService.post(author, secondKluchText);
		assertThat(anotherKluch, notNullValue());
		assertThat(anotherKluch.getAuthor().getId(), equalTo(user.getId()));
		assertThat(anotherKluch.getText(), equalTo(secondKluchText));
	}

	@Test
	public void testSameContentDifferentAuthor() throws Exception {
		String kluchText = generateString(50);
		String author = "author";
		User user = mockUser(author);
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthor().getId(), equalTo(user.getId()));
		assertThat(firstKluch.getText(), equalTo(kluchText));
		String anotherAuthor = "author2";
		User anotherUser = mockUser(anotherAuthor);
		Kluch anotherKluch = kluchService.post(anotherAuthor, kluchText);
		assertThat(anotherKluch, notNullValue());
		assertThat(anotherKluch.getAuthor().getId(), equalTo(anotherUser.getId()));
		assertThat(anotherKluch.getText(), equalTo(kluchText));
	}

	@Test(expected = NullOrEmptyException.class)
	public void testDeleteKluchUsernameNull() throws Exception {
		kluchService.deleteKluch(null, 0L);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testDeleteKluchUsernameEmpty() throws Exception {
		kluchService.deleteKluch("", 0L);
	}

	@Test(expected = UserDoesNotExistException.class)
	public void testDeleteKluchUserDoesNotExist() throws Exception {
		kluchService.deleteKluch("lolers", 0L);
	}

	@Test(expected = KluchDoesNotExistException.class)
	public void testDeleteKluchKluchDoesNotExist() throws Exception {
		String username = "lolers";
		mockUser(username);
		kluchService.deleteKluch(username, 0L);
	}

	@Test(expected = InvalidOwnerException.class)
	public void testDeleteKluchNotOwner() throws Exception {
		String username = "lolers";
		mockUser(username);
		String differentUsername = "danny";
		User anotherUser = mockUser(differentUsername);
		Kluch kluch = postKluch(anotherUser, "text");
		
		kluchService.deleteKluch(username, kluch.getId());
	}

	@Test
	public void deleteKluchEverythingValid() {
		String username = "lolers";
		User user = mockUser(username);
		Kluch kluch = postKluch(user, generateString(50));
		kluchService.deleteKluch(username, kluch.getId());
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testLikeKluchNullUsername() {
		kluchService.likeKluch(null, 0L);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testLikeKluchEmptyUsername() {
		kluchService.likeKluch("", 0L);
	}
	
	@Test(expected = KluchDoesNotExistException.class)
	public void testLikeKluchKluchDoesNotExist() {
		mockUser("user");
		kluchService.likeKluch("user", 0L);
	}
	
	@Test
	public void testLikeKluchValid() {
		mockUser("user");
		User liked = mockUser("liked");
		Kluch kluch = postKluch(liked, "text");
		int likes = kluchService.likeKluch("user", kluch.getId());
		assertThat(likes, equalTo(1));
	}
	
	@Test
	public void testLikeKluchValidTwice() {
		User user = mockUser("user");
		mockUser("secondUser");
		mockUser("liked");
		Kluch kluch = postKluch(user, generateString(50));
		int liked = kluchService.likeKluch("user", kluch.getId());
		assertThat(liked, equalTo(1));
		liked = kluchService.likeKluch("secondUser", kluch.getId());
		assertThat(liked, equalTo(2));
	}
	
	@Test
	public void testLikeKluchValidTwiceSameUser() {
		User user = mockUser("user");
		mockUser("liked");
		Kluch kluch = postKluch(user, generateString(50));
		int liked = kluchService.likeKluch("user", kluch.getId());
		assertThat(liked, equalTo(1));
		liked = kluchService.likeKluch("user", kluch.getId());
		assertThat(liked, equalTo(1));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testUnlikeKluchNullUsername() {
		kluchService.unlikeKluch(null, 0L);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testUnlikeKluchEmptyUsername() {
		kluchService.unlikeKluch("", 0L);
	}
	
	@Test(expected = KluchDoesNotExistException.class)
	public void testUnlikeKluchKluchDoesNotExist() {
		mockUser("user");
		kluchService.unlikeKluch("user", 0L);
	}
	
	@Test
	public void testUnlikeKluchValid() {
		User user = mockUser("user");
		User anotherUser = mockUser("liked");
		Kluch kluch = postKluch(anotherUser, generateString(50));
		kluch.getLikes().add(user.getId());
		
		int liked = kluchService.unlikeKluch("user", kluch.getId());
		assertThat(liked, equalTo(0));
	}
	
	@Test
	public void testUnlikeKluchValidTwice() {
		User user = mockUser("user");
		User secondUser = mockUser("secondUser");
		User anotherUser = mockUser("liked");
		Kluch kluch = postKluch(anotherUser, generateString(50));
		kluch.getLikes().add(user.getId());
		kluch.getLikes().add(secondUser.getId());
		
		int liked = kluchService.unlikeKluch("user", kluch.getId());
		assertThat(liked, equalTo(1));
		liked = kluchService.unlikeKluch("secondUser", kluch.getId());
		assertThat(liked, equalTo(0));
	}
	
	@Test
	public void testUnlikeKluchValidTwiceSameUser() {
		User user = mockUser("user");
		User anotherUser = mockUser("liked");
		Kluch kluch = postKluch(anotherUser, generateString(50));
		kluch.getLikes().add(user.getId());
		
		int liked = kluchService.unlikeKluch("user", kluch.getId());
		assertThat(liked, equalTo(0));
		liked = kluchService.unlikeKluch("user", kluch.getId());
		assertThat(liked, equalTo(0));
	}
	
	private Kluch postKluch(User user, String text) {
		return kluchService.post(user.getUsername(), text);
	}

	private String generateString(int length) {
		if (length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(length);
		while(sb.length() < length) {
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString();
			if(sb.capacity() < uuidString.length()) {
				sb.append(uuidString.substring(0, sb.capacity()));
			} else {
				sb.append(uuidString);
			}
		}
		return sb.toString();
	}

}
