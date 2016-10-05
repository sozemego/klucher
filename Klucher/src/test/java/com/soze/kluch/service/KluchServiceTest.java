package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class KluchServiceTest extends TestWithMockUsers {

	@MockBean
	private KluchDao kluchDao;
	
	@MockBean
	private KluchAssembler assembler;

	@Autowired
	@InjectMocks
	private KluchService kluchService;
	
	private final AtomicLong ids = new AtomicLong(1);

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
		Kluch kluch = getKluch(user, kluchText);
		when(assembler.assembleKluch(user, kluchText)).thenReturn(kluch);
		when(kluchDao.save(kluch)).thenReturn(kluch);
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthorId(), equalTo(user.getId()));
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
		Kluch kluch = getKluch(user, kluchText);
		when(assembler.assembleKluch(user, kluchText)).thenReturn(kluch);
		when(kluchDao.save(kluch)).thenReturn(kluch);
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthorId(), equalTo(user.getId()));
		assertThat(firstKluch.getText(), equalTo(kluchText));
		String secondKluchText = generateString(51);
		kluch = getKluch(user, secondKluchText);
		when(kluchDao.save(kluch)).thenReturn(kluch);
		when(assembler.assembleKluch(user, secondKluchText)).thenReturn(kluch);
		Kluch anotherKluch = kluchService.post(author, secondKluchText);
		assertThat(anotherKluch, notNullValue());
		assertThat(anotherKluch.getAuthorId(), equalTo(user.getId()));
		assertThat(anotherKluch.getText(), equalTo(secondKluchText));
	}

	@Test
	public void testSameContentDifferentAuthor() throws Exception {
		String kluchText = generateString(50);
		String author = "author";
		User user = mockUser(author);
		Kluch kluch = getKluch(user, kluchText);
		when(assembler.assembleKluch(user, kluchText)).thenReturn(kluch);
		when(kluchDao.save(kluch)).thenReturn(kluch);
		Kluch firstKluch = kluchService.post(author, kluchText);
		assertThat(firstKluch, notNullValue());
		assertThat(firstKluch.getAuthorId(), equalTo(user.getId()));
		assertThat(firstKluch.getText(), equalTo(kluchText));
		String anotherAuthor = "author2";
		User anotherUser = mockUser(anotherAuthor);
		kluch = getKluch(anotherUser, kluchText);
		when(kluchDao.save(kluch)).thenReturn(kluch);
		when(assembler.assembleKluch(anotherUser, kluchText)).thenReturn(kluch);
		Kluch anotherKluch = kluchService.post(anotherAuthor, kluchText);
		assertThat(anotherKluch, notNullValue());
		assertThat(anotherKluch.getAuthorId(), equalTo(anotherUser.getId()));
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
		Kluch kluch = getKluch(anotherUser, "text");
		when(kluchDao.findOne(kluch.getId())).thenReturn(kluch);
		kluchService.deleteKluch(username, kluch.getId());
	}

	@Test
	public void deleteKluchEverythingValid() {
		String username = "lolers";
		User user = mockUser(username);
		Kluch kluch = getKluch(user, "text");
		when(kluchDao.findOne(kluch.getId())).thenReturn(kluch);
		kluchService.deleteKluch(username, kluch.getId());
		verify(kluchDao).delete(kluch);
	}

	private Kluch getKluch(User user, String text) {
		Kluch kluch = new Kluch(user.getId(), text, null);
		kluch.setId(ids.getAndIncrement());
		return kluch;
	}

	private String generateString(int length) {
		if (length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append("c");
		}
		return sb.toString();
	}

}
