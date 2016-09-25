package com.soze.feed.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.InvalidTimestampException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.feed.model.KluchFeedElement;
import com.soze.feed.service.FeedConstructor.FeedDirection;
import com.soze.hashtag.dao.HashtagDao;
import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.MentionNotification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FeedConstructorTest extends TestWithMockUsers {
  
  private static final int BEFORE_KLUCHS_PER_REQUEST = 30;
  private static final int AFTER_KLUCHS_PER_REQUEST = 30;
  private final PageRequest before = new PageRequest(0, BEFORE_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.DESC, "timestamp")));
  private final PageRequest after = new PageRequest(0, AFTER_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.ASC, "timestamp")));
  private final PageRequest exists = new PageRequest(0, 1);
  
  @Autowired
  @InjectMocks
  private FeedConstructor constructor;

  @MockBean
  private KluchDao kluchDao;
  
  @MockBean
  private HashtagDao hashtagDao;
  
  @Autowired
  private UserDao userDao;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
  }
  
  @Test(expected = UserDoesNotExistException.class)
  public void testInvalidUser() {
    constructor.constructFeed("doesNotExist", 0, false);
  }

  @Test(expected = NullOrEmptyException.class)
  public void testInvalidUserEmptyName() {
    constructor.constructFeed("", 0, false);
  }

  @Test
  public void testNoKluchs() {
    mockUser("test", "password");
    when(kluchDao.findByAuthorInAndTimestampLessThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(0)),
        eq(before)))
    .thenReturn(Arrays.asList());
    Feed<KluchFeedElement> feed = constructor.constructFeed("test", 0, false);
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
  }

  @Test
  public void testExistAfterNoKluchs() {
    mockUser("test", "password");
    when(kluchDao.findByAuthorInAndTimestampGreaterThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(0)),
        eq(exists)))
    .thenReturn(Arrays.asList());
    boolean exists = constructor.existsFeedAfter("test", 0, false);
    assertThat(exists, equalTo(false));
  }

  @Test
  public void testExistsKluchs() {
    mockUser("test", "password");
    when(kluchDao.findByAuthorInAndTimestampGreaterThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(0)),
        eq(exists)))
    .thenReturn(Arrays.asList(new Kluch()));
    boolean exists = constructor.existsFeedAfter("test", 0, false);
    assertThat(exists, equalTo(true));
  }

  @Test
  public void testDoNotExistsKluchs() {
    mockUser("test", "password");
    when(kluchDao.findByAuthorInAndTimestampGreaterThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(Long.MAX_VALUE)),
        eq(exists)))
    .thenReturn(Arrays.asList());
    boolean exists = constructor.existsFeedAfter("test", Long.MAX_VALUE, false);
    assertThat(exists, equalTo(false));
  }
  
  @Test
  public void getOneKluchBefore() {
    mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByAuthorInAndTimestampLessThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(Long.MAX_VALUE)),
        eq(before)))
    .thenReturn(randomKluchs);
    Feed<KluchFeedElement> feed = constructor.constructFeed("test", Long.MAX_VALUE, false, FeedDirection.BEFORE);
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs, notNullValue());
    assertThat(kluchs.size(), equalTo(1));
  }
  
  @Test
  public void getManyKluchsBefore() {
    mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByAuthorInAndTimestampLessThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(Long.MAX_VALUE)),
        eq(before)))
    .thenReturn(randomKluchs);
    Feed<KluchFeedElement> feed = constructor.constructFeed("test", Long.MAX_VALUE, false, FeedDirection.BEFORE);
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs, notNullValue());
    assertThat(kluchs.size(), equalTo(30));
  }
  
  @Test
  public void getOneKluchAfter() {
    mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByAuthorInAndTimestampGreaterThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(0)),
        eq(after)))
    .thenReturn(randomKluchs);
    Feed<KluchFeedElement> feed = constructor.constructFeed("test", 0, false, FeedDirection.AFTER);
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs, notNullValue());
    assertThat(kluchs.size(), equalTo(1));
  }
  
  @Test
  public void getManyKluchsAfter() {
    mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByAuthorInAndTimestampGreaterThan(
        eq(Arrays.asList("test")),
        eq(new Timestamp(0)),
        eq(after)))
    .thenReturn(randomKluchs);
    Feed<KluchFeedElement> feed = constructor.constructFeed("test", 0, false, FeedDirection.AFTER);
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs, notNullValue());
    assertThat(kluchs.size(), equalTo(30));
  }

  @Test(expected = NullOrEmptyException.class)
  public void testInvalidUsername() {
    constructor.constructFeed(null, 0, false);
  }

  @Test(expected = UserDoesNotExistException.class)
  public void testNonExistentUsername() {
    constructor.constructFeed("iDontExist", 0, false);
  }
  
  @Test
  public void testHashtagFeedNotExistent() {
    String hashtagText = "#hashtag";
    when(kluchDao.findByHashtagsInAndTimestampLessThan(
        eq(new Hashtag(hashtagText)),
        eq(new Timestamp(0)),
        eq(before)))
    .thenReturn(Arrays.asList());
    Feed<KluchFeedElement> feed = constructor.constructHashtagFeed(hashtagText, 0);
    assertThat(feed, notNullValue());
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
  }
  
  @Test
  public void testHashtagNotExistentNoPoundSign() {
    String hashtagText = "hashtag";
    when(kluchDao.findByHashtagsInAndTimestampLessThan(
        eq(new Hashtag(hashtagText)),
        eq(new Timestamp(0)),
        eq(before)))
    .thenReturn(Arrays.asList());
    Feed<KluchFeedElement> feed = constructor.constructHashtagFeed(hashtagText, 0);
    assertThat(feed, notNullValue());
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
  }
  
  @Test
  public void testHashtagFeedValid() {
    String hashtagText = "#hashtag";
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByHashtagsInAndTimestampLessThan(
        eq(new Hashtag(hashtagText)),
        eq(new Timestamp(0)),
        eq(before)))
    .thenReturn(randomKluchs);
    when(hashtagDao.findOne(hashtagText)).thenReturn(new Hashtag(hashtagText));
    Feed<KluchFeedElement> feed = constructor.constructHashtagFeed(hashtagText, 0);
    assertThat(feed, notNullValue());
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(1));
  }
  
  @Test
  public void testHashtagFeedValidManyKluchs() {
    String hashtagText = "#hashtag";
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByHashtagsInAndTimestampLessThan(
        eq(new Hashtag(hashtagText)),
        eq(new Timestamp(0)),
        eq(before)))
    .thenReturn(randomKluchs);
    when(hashtagDao.findOne(hashtagText)).thenReturn(new Hashtag(hashtagText));
    Feed<KluchFeedElement> feed = constructor.constructHashtagFeed(hashtagText, 0);
    assertThat(feed, notNullValue());
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(30));
  }
  
  @Test
  public void getKluchsDontExist() throws Exception {
  	Feed<KluchFeedElement> feed = constructor.getKluchs(Arrays.asList(1L, 2L));
  	assertThat(feed.getElements().size(), equalTo(0));
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testGetMentionsNullUsername() throws Exception {
  	constructor.getMentions(null, 0);
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testGetMentionsEmptyUsername() throws Exception {
  	constructor.getMentions("", 0);
  }
  
  @Test(expected = UserDoesNotExistException.class)
  public void testGetMentionsUserDoesNotExist() throws Exception {
  	constructor.getMentions("user", 0);
  }
  
  @Test(expected = InvalidTimestampException.class)
  public void testGetMentionsInvalidTimestamp() throws Exception {
  	mockUser("user");
  	constructor.getMentions("user", -5);
  }
  
  @Test
  public void testGetMentionsNoMentions() throws Exception {
  	mockUser("user");
  	Feed<KluchFeedElement> feed = constructor.getMentions("user", Long.MAX_VALUE);
  	assertThat(feed.getElements().size(), equalTo(0));
  }
  
  @Test
  public void testGetMentionsFewMentions() throws Exception {
  	User user = mockUser("user");
  	user.getMentionNotifications().add(new MentionNotification(1L, 0L));
  	user.getMentionNotifications().add(new MentionNotification(2L, 1L));
  	user.getMentionNotifications().add(new MentionNotification(3L, 2L));
  	List<User> users = mockUsers(Arrays.asList("test1", "test2", "test3"));
  	when(userDao.findAll(Arrays.asList("test1", "test2", "test3"))).thenReturn(users);
  	Kluch kluch1 = mock(Kluch.class);
  	when(kluch1.getId()).thenReturn(1L);
  	when(kluch1.getAuthor()).thenReturn("test1");
  	Kluch kluch2 = mock(Kluch.class);
  	when(kluch2.getId()).thenReturn(2L);
  	when(kluch2.getAuthor()).thenReturn("test2");
  	Kluch kluch3 = mock(Kluch.class);
  	when(kluch3.getId()).thenReturn(3L);
  	when(kluch3.getAuthor()).thenReturn("test3");
  	List<Kluch> kluchs = Arrays.asList(kluch1, kluch2, kluch3);
  	when(kluchDao.findAll(Arrays.asList(1L, 2L, 3L))).thenReturn(kluchs);
  	Feed<KluchFeedElement> feed = constructor.getMentions("user", Long.MAX_VALUE);
  	assertThat(feed.getElements().size(), equalTo(3));
  }
  
  @Test
  public void testGetMentionsOnceMoreThanLimit() throws Exception {
  	User user = mockUser("user");
  	int mentions = 35;
  	for(int i = 0; i < mentions; i++) {
  		user.getMentionNotifications().add(new MentionNotification(i, Long.MAX_VALUE - i - 1));
  	}
  	List<User> users = mockUsers(Arrays.asList("test1"));
  	when(userDao.findAll(Arrays.asList("test1"))).thenReturn(users);
  	List<Kluch> kluchs = new ArrayList<>();
  	for(int i = 0; i < 30; i++) {
  		Kluch kluch = mock(Kluch.class);
  		when(kluch.getId()).thenReturn(0L + i);
    	when(kluch.getAuthor()).thenReturn("test1");
    	kluchs.add(kluch);
  	}
  	when(kluchDao.findAll(argThat(sameAsSet(Arrays.asList(
  			0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
  			11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L,
  			21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L))))).thenReturn(kluchs);
  	Feed<KluchFeedElement> feed = constructor.getMentions("user", Long.MAX_VALUE);
  	assertThat(feed.getElements().size(), equalTo(30));
  }
  
  @Test
  public void testGetMentionsTwiceMoreThanLimit() throws Exception {
  	User user = mockUser("user");
  	int mentions = 35;
  	int offset = 1;
  	for(int i = 0; i < mentions; i++) {
  		user.getMentionNotifications().add(new MentionNotification(i, Long.MAX_VALUE - i - offset));
  	}
  	
  	List<User> users = mockUsers(Arrays.asList("test1"));
  	when(userDao.findAll(Arrays.asList("test1"))).thenReturn(users);
  	List<Kluch> kluchs = new ArrayList<>();
  	for(int i = 0; i < 30; i++) {
  		Kluch kluch = mock(Kluch.class);
  		when(kluch.getId()).thenReturn(0L + i);
    	when(kluch.getAuthor()).thenReturn("test1");
    	kluchs.add(kluch);
  	}
  	when(kluchDao.findAll(argThat(sameAsSet(Arrays.asList(
  			0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
  			11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L,
  			21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L))))).thenReturn(kluchs);
  	Feed<KluchFeedElement> feed = constructor.getMentions("user", Long.MAX_VALUE);
  	assertThat(feed.getElements().size(), equalTo(30));
  	List<Kluch> laterKluchs = new ArrayList<>();
  	for(int i = 30; i < mentions; i++) {
  		Kluch kluch = mock(Kluch.class);
  		when(kluch.getId()).thenReturn(0L + i);
    	when(kluch.getAuthor()).thenReturn("test1");
    	laterKluchs.add(kluch);
  	}
  	when(kluchDao.findAll(argThat(sameAsSet(Arrays.asList(
  			30L, 31L, 32L, 33L, 34L))))).thenReturn(laterKluchs);
  	long lastTimestamp = Long.MAX_VALUE - 30;
  	Feed<KluchFeedElement> laterFeed = constructor.getMentions("user", lastTimestamp);
  	assertThat(laterFeed.getElements().size(), equalTo(5));
  }
  
  
  private List<Kluch> getRandomKluchs(int number) {
    List<Kluch> kluchs = new ArrayList<>();
    List<String> accumulatedUsernames = new ArrayList<>();
    for(int i = 0; i < number; i++) {
    	Kluch kluch = new Kluch();
    	String randomUsername = getRandomUsername();
    	accumulatedUsernames.add(randomUsername);
    	kluch.setAuthor(randomUsername);
      kluchs.add(kluch);
    }
    mockUsers(accumulatedUsernames);
    return kluchs;
  }
  
  private String getRandomUsername() {
  	Random random = new Random();
  	String alphabet = "qwertyuiopasdfghjklzxcvbnm";	
  	int randomUsernameLength = random.nextInt(12) + 1;
  	String username = "";
  	for(int i = 0; i < randomUsernameLength; i++) {
  		int randomIndex = random.nextInt(alphabet.length());
  		username += alphabet.charAt(randomIndex);
  	}
  	return username;
  }

}
