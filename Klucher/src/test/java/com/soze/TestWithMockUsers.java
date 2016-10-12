package com.soze;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterConverter;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * Tests which require users be present in a database/be logged in
 * should extend this class and use methods contained therein.
 * @author sozek
 *
 */
public class TestWithMockUsers {

	private final Random random = new Random();
	
	private final AtomicLong ids = new AtomicLong(1);
	
	private final Map<String, Long> savedUserIds = new HashMap<>();
	
  @MockBean
  private UserDao userDao;
  
  @Autowired
  private RegisterConverter registerConverter;
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }
  
  protected User mockUser(String username) {
  	return mockUser(username, "password");
  }
  
  protected User mockUser(String username, boolean login) {
  	return mockUser(username, "password", login);
  }
  
  protected User mockUser(String username, String password) {
    return mockUser(username, password, false);
  }
  
  protected User mockUser(String username, String password, boolean login) {
    User user = getBaseUser(username, password);
    when(userDao.findOne(username)).thenReturn(user);
    when(userDao.findOne(user.getId())).thenReturn(user);
    if(login) {
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(username,
              user.getHashedPassword(), user.getAuthorities()));
    }
    return user;
  }
  
  protected List<User> mockUsers(List<String> usernames) {
  	return mockUsers(usernames, false);
  }
  
	protected List<User> mockUsers(List<String> usernames, boolean alreadyExist) {
  	if(alreadyExist) {
  		mockExistingUsers(usernames);
  	}
  	return mockNewUsers(usernames);
  }
	
	protected List<User> mockNewUsers(List<String> usernames) {
		List<User> users = new ArrayList<>();
  	List<Long> ids = new ArrayList<>();
  	for(String username: usernames) {
  		User user = mockUser(username, "password", false);
  		users.add(user);
  		ids.add(user.getId());
  	}
		//when(userDao.findAll(usernames)).thenReturn(users);
  	when(userDao.findByUsernameIn(argThat(sameAsSet(usernames)))).thenReturn(users);
		when(userDao.findAll(argThat(sameAsSet(ids)))).thenReturn(users);
  	return users;
	}
	
	protected List<User> mockExistingUsers(List<String> usernames) {
		List<User> users = new ArrayList<>();
		List<Long> userIds = new ArrayList<>();
		for(String username: usernames) {
			User user = userDao.findOne(username);
			users.add(user);
			userIds.add(user.getId());
		}
		when(userDao.findByUsernameIn(argThat(sameAsSet(usernames)))).thenReturn(users);
		when(userDao.findAll(argThat(sameAsSet(userIds)))).thenReturn(users);
		return users;
	}

  private User getBaseUser(String username, String password) {
  	Long id = null;
  	if(savedUserIds.containsKey(username)) {
  		id = savedUserIds.get(username);
  	} else {
  		id = ids.getAndIncrement();
  		savedUserIds.put(username, id);
  	}
    RegisterForm form = new RegisterForm();
    form.setUsername(username);
    form.setPassword(password);
    User testUser = registerConverter.convertRegisterForm(form);
    testUser.setId(id);
    return testUser;
  }
  
  protected User mockRandomUser() {
  	String username = getRandomUsername();
  	return mockUser(username, false);
  }
  
  private String getRandomUsername() {
  	String alphabet = "qwertyuiopasdfghjklzxcvbnm";	
  	int randomUsernameLength = random.nextInt(12) + 1;
  	String username = "";
  	for(int i = 0; i < randomUsernameLength; i++) {
  		int randomIndex = random.nextInt(alphabet.length());
  		username += alphabet.charAt(randomIndex);
  	}
  	return username;
  }
  
	@SuppressWarnings("unchecked")
	// http://stackoverflow.com/a/25700998/5017419
	protected <T> Matcher<Iterable<T>> sameAsSet(Iterable<T> expected) {
		return new BaseMatcher<Iterable<T>>() {
			@Override
			public boolean matches(Object o) {
				if(o == null) {
					return false;
				}
				Iterable<T> actualList = Collections.EMPTY_LIST;
				try {
					actualList = (Iterable<T>) o;
				} catch (ClassCastException e) {
					return false;
				}
				Set<T> expectedSet = new HashSet<T>((Collection<? extends T>) expected);
				Set<T> actualSet = new HashSet<T>((Collection<? extends T>) actualList);
				boolean equals = actualSet.equals(expectedSet);
				return equals;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("should contain all and only elements of ").appendValue(expected);
			}
		};
	}

}
