package com.soze.kluch.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
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
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.FeedRequest;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchFeedElement;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class KluchFeedServiceTest extends TestWithMockUsers {
  
  private static final int ELEMENTS_PER_REQUEST = 30;
  private final PageRequest next = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.DESC, "timestamp")));
	private final PageRequest previous = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.ASC, "timestamp")));
  private final PageRequest exists = new PageRequest(0, 1);
  
  @Autowired
  @InjectMocks
  private KluchFeedService feedService;

  @MockBean
  private KluchDao kluchDao;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testExistAfterNoKluchs() {
  	User user = mockUser("test", "password");
    when(kluchDao.findByAuthorIdInAndIdGreaterThan(
        eq(Arrays.asList(user.getId())),
        eq(0L),
        eq(exists)))
    .thenReturn(new PageImpl<>(Arrays.asList()));
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(false));
  }

  @Test
  public void testExistsKluchs() {
  	User user = mockUser("test", "password");
    when(kluchDao.findByAuthorIdInAndIdGreaterThan(
        eq(Arrays.asList(user.getId())),
        eq(0L),
        eq(exists)))
    .thenReturn(new PageImpl<>(Arrays.asList(new Kluch(0, null, null))));
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(true));
  }

  @Test
  public void testDoNotExistsKluchs() {
  	User user = mockUser("test", "password");
    when(kluchDao.findByAuthorIdInAndIdGreaterThan(
        eq(Arrays.asList(user.getId())),
        eq(0L),
        eq(exists)))
    .thenReturn(new PageImpl<>(Arrays.asList()));
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(false));
  }
  
  @Test
  public void getOneKluchBefore() {
  	User user = mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByAuthorIdInAndIdLessThan(
        eq(Arrays.asList(user.getId())),
        eq(Long.MAX_VALUE),
        eq(next)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructFeed("test", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()), false);
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(1L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(1));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void getManyKluchsBefore() {
    User user = mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByAuthorIdInAndIdLessThan(
        eq(Arrays.asList(user.getId())),
        eq(Long.MAX_VALUE),
        eq(next)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructFeed("test", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()), false);
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(30L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(30));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void getOneKluchPrevious() {
  	User user = mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByAuthorIdInAndIdGreaterThan(
        eq(Arrays.asList(user.getId())),
        eq(0L),
        eq(previous)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructFeed("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(1L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(1));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void getManyKluchsPrevious() {
  	User user = mockUser("test", "password");
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByAuthorIdInAndIdGreaterThan(
        eq(Arrays.asList(user.getId())),
        eq(0L),
        eq(previous)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructFeed("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(30L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(30));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testHashtagFeedNotExistent() {
    String hashtagText = "hashtag";
    when(kluchDao.findByHashtagsInAndIdLessThan(
        eq(hashtagText),
        eq(0L),
        eq(next)))
    .thenReturn(new PageImpl<>(Arrays.asList()));
    Feed<KluchFeedElement> feed = feedService.constructHashtagFeed(hashtagText, new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()));
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), nullValue());
    assertThat(feed.getTotalElements(), equalTo(0L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testHashtagNotExistentNoPoundSign() {
    String hashtagText = "hashtag";
    when(kluchDao.findByHashtagsInAndIdLessThan(
        eq(hashtagText),
        eq(Long.MAX_VALUE),
        eq(next)))
    .thenReturn(new PageImpl<>(Arrays.asList()));
    Feed<KluchFeedElement> feed = feedService.constructHashtagFeed(hashtagText, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), nullValue());
    assertThat(feed.getTotalElements(), equalTo(0L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testHashtagFeedValid() {
    String hashtagText = "hashtag";
    List<Kluch> randomKluchs = getRandomKluchs(1);
    when(kluchDao.findByHashtagsInAndIdLessThan(
        eq(hashtagText),
        eq(Long.MAX_VALUE),
        eq(next)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructHashtagFeed(hashtagText, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(1L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(1));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testHashtagFeedValidManyKluchs() {
    String hashtagText = "hashtag";
    List<Kluch> randomKluchs = getRandomKluchs(30);
    when(kluchDao.findByHashtagsInAndIdLessThan(
        eq(hashtagText),
        eq(Long.MAX_VALUE),
        eq(next)))
    .thenReturn(new PageImpl<>(randomKluchs));
    Feed<KluchFeedElement> feed = feedService.constructHashtagFeed(hashtagText, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
    assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(30L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(30));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testGetMentionsNullUsername() throws Exception {
  	feedService.getMentions(null, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  }
  
  @Test(expected = NullOrEmptyException.class)
  public void testGetMentionsEmptyUsername() throws Exception {
  	feedService.getMentions("", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  }
  
  @Test(expected = UserDoesNotExistException.class)
  public void testGetMentionsUserDoesNotExist() throws Exception {
  	feedService.getMentions("user", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  }
  
  @Test
  public void testGetMentionsNoMentions() throws Exception {
  	mockUser("user");
  	when(kluchDao.findByMentionsInAndIdLessThan(
  			eq("user"),
  			eq(Long.MAX_VALUE),
  			eq(next)))
  	.thenReturn(new PageImpl<>(Arrays.asList()));
  	Feed<KluchFeedElement> feed = feedService.getMentions("user", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  	assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), nullValue());
    assertThat(feed.getTotalElements(), equalTo(0L));
    Collection<KluchFeedElement> kluchs = feed.getElements();
    assertThat(kluchs.size(), equalTo(0));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testGetMentionsFewMentions() throws Exception {
  	mockUser("user");
  	List<Kluch> kluchs = getRandomKluchs(15);
  	when(kluchDao.findByMentionsInAndIdLessThan(
  			eq("user"),
  			eq(Long.MAX_VALUE),
  			eq(next)))
  	.thenReturn(new PageImpl<>(kluchs));
  	Feed<KluchFeedElement> feed = feedService.getMentions("user", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  	assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(15L));
    assertThat(feed.getElements().size(), equalTo(15));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  @Test
  public void testGetMentionsOnce() throws Exception {
  	mockUser("user");
  	List<Kluch> kluchs = getRandomKluchs(30);
  	when(kluchDao.findByMentionsInAndIdLessThan(
  			eq("user"),
  			eq(Long.MAX_VALUE),
  			eq(next)))
  	.thenReturn(new PageImpl<>(kluchs));
  	Feed<KluchFeedElement> feed = feedService.getMentions("user", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  	assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(30L));
    assertThat(feed.getElements().size(), equalTo(30));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  private List<Kluch> getRandomKluchs(int number) {
    List<Kluch> kluchs = new ArrayList<>();
    List<String> accumulatedUsernames = new ArrayList<>();
    for(int i = 0; i < number; i++) {
    	String randomUsername = getRandomUsername();
    	accumulatedUsernames.add(randomUsername);
    	User user = mockUser(randomUsername);
    	Kluch kluch = new Kluch(user.getId(), null, new Timestamp(0L + i));
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
