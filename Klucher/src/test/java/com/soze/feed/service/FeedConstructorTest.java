package com.soze.feed.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.feed.model.KluchFeedElement;
import com.soze.feed.service.FeedConstructor.FeedDirection;
import com.soze.hashtag.dao.HashtagDao;
import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;


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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
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
    List<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(30));
  }
  
  @Test
  public void getKluchsDontExist() throws Exception {
  	Feed<KluchFeedElement> feed = constructor.getKluchs(Arrays.asList(1L, 2L));
  	assertThat(feed.getElements().size(), equalTo(0));
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
