package com.soze.user.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest extends TestWithMockUsers {

	@Autowired
	private UserService service;
	
	@Autowired
	private UserDao userDao;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testLikeUsernameNull() throws Exception {
		service.like(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testLikeUsernameEmpty() throws Exception {
		service.like("", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testLikeUsernameDoesNotExist() throws Exception {
		service.like("user", "anotherUser");
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testLikeSameUser() throws Exception {
		mockUser("user");
		service.like("user", "user");
	}
	
	@Test
	public void testValidLike() throws Exception {
		mockUser("user");
		User likedUser = mockUser("likedUser");
		int likes = service.like("user", "likedUser");
		assertThat(likes, equalTo(1));
		verify(userDao).save(likedUser);
	}
	
	@Test
	public void testValidLikes() throws Exception {
		mockUser("likedUser");
		List<User> likingUsers = mockUsers(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
		int likes = 0;
		for(User user: likingUsers) {
			likes = service.like(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(8));
		verify(userDao, times(8)).save(any(User.class));
	}
	
	@Test
	public void testValidLikesMultiple() throws Exception {
		mockUser("likedUser");
		List<User> likingUsers = mockUsers(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
		int likes = 0;
		for(User user: likingUsers) {
			service.like(user.getUsername(), "likedUser");
			likes = service.like(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(8));
		verify(userDao, times(8)).save(any(User.class));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testUnlikeUsernameNull() throws Exception {
		service.unlike(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testUnlikeUsernameEmpty() throws Exception {
		service.unlike("", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testUnlikeUsernameDoesNotExist() throws Exception {
		service.unlike("user", "anotherUser");
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testUnlikeSameUser() throws Exception {
		mockUser("user");
		service.unlike("user", "user");
	}
	
	@Test
	public void testValidUnlike() throws Exception {
		mockUser("user");
		User likedUser = mockUser("likedUser");
		int likes = service.like("user", "likedUser");
		assertThat(likes, equalTo(1));
		verify(userDao, times(1)).save(likedUser);
		likes = service.unlike("user", "likedUser");
		assertThat(likes, equalTo(0));
		verify(userDao, times(2)).save(likedUser);
	}
	
	@Test
	public void testValidUnlikes() throws Exception {
		mockUser("likedUser");
		List<User> likingUsers = mockUsers(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
		int likes = 0;
		for(User user: likingUsers) {
			likes = service.like(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(8));
		verify(userDao, times(8)).save(any(User.class));
		for(User user: likingUsers) {
			likes = service.unlike(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(0));
		verify(userDao, times(16)).save(any(User.class));
	}
	
	@Test
	public void testValidUnlikesMultiple() throws Exception {
		mockUser("likedUser");
		List<User> likingUsers = mockUsers(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
		int likes = 0;
		for(User user: likingUsers) {
			likes = service.like(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(8));
		verify(userDao, times(8)).save(any(User.class));
		for(User user: likingUsers) {
			service.unlike(user.getUsername(), "likedUser");
			likes = service.unlike(user.getUsername(), "likedUser");
		}
		assertThat(likes, equalTo(0));
		verify(userDao, times(16)).save(any(User.class));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testDoesLikeNullUsername() throws Exception {
		service.doesLike(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testDoesLikeEmptyUsername() throws Exception {
		service.doesLike("", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testDoesLikeUserDoesNotExist() throws Exception {
		service.doesLike("user", "anotherUser");
	}
	
	@Test
	public void testDoesLikeUserExistsSameUser() throws Exception {
		mockUser("user");
		boolean doesLike = service.doesLike("user", "user");
		assertThat(doesLike, equalTo(false));
	}
	
	@Test
	public void testDoesLikeDifferentUser() throws Exception {
		mockUser("user");
		mockUser("anotherUser");
		boolean doesLike = service.doesLike("user", "anotherUser");
		assertThat(doesLike, equalTo(false));
	}
	
	@Test
	public void testDoesLikeDifferentUserLikes() throws Exception {
		User user = mockUser("user");
		User anotherUser = mockUser("anotherUser");
		anotherUser.getLikes().add(user.getId());
		boolean doesLike = service.doesLike("user", "anotherUser");
		assertThat(doesLike, equalTo(true));
	}
	
	@Test
	public void testDoesLikeDifferentUserDoesNotLike() throws Exception {
		User user = mockUser("user");
		User anotherUser = mockUser("anotherUser");
		user.getLikes().add(anotherUser.getId());
		boolean doesLike = service.doesLike("user", "anotherUser");
		assertThat(doesLike, equalTo(false));
	}
 	
}
