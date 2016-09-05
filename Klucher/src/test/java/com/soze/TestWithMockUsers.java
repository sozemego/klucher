package com.soze;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    if(login) {
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(username,
              user.getHashedPassword(), user.getAuthorities()));
    }
    return user;
  }
  
	protected List<User> mockUsers(List<String> usernames) {
  	List<User> users = new ArrayList<>();
  	for(String username: usernames) {
  		users.add(mockUser(username, "password", false));
  	}
		//when(userDao.findAll(usernames)).thenReturn(users);
		when(userDao.findAll(argThat(sameAsSet(usernames)))).thenReturn(users);
  	return users;
  }

  private User getBaseUser(String username, String password) {
    RegisterForm form = new RegisterForm();
    form.setUsername(username);
    form.setPassword(password);
    User testUser = registerConverter.convertRegisterForm(form);
    return testUser;
  }
  
	@SuppressWarnings("unchecked")
	// http://stackoverflow.com/a/25700998/5017419
	protected <T> Matcher<List<T>> sameAsSet(List<T> expected) {
		return new BaseMatcher<List<T>>() {
			@Override
			public boolean matches(Object o) {

				List<T> actualList = Collections.EMPTY_LIST;
				try {
					actualList = (List<T>) o;
				} catch (ClassCastException e) {
					return false;
				}
				Set<T> expectedSet = new HashSet<T>(expected);
				Set<T> actualSet = new HashSet<T>(actualList);
				return actualSet.equals(expectedSet);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("should contain all and only elements of ").appendValue(expected);
			}
		};
	}

}
