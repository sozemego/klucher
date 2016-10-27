package com.soze.kluch.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.kluch.model.FeedRequest;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchFeedElement;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class KluchFeedServiceTest extends TestWithMockUsers {
  
  @Autowired
  @InjectMocks
  private KluchFeedService feedService;
  
  @Autowired
  private KluchService kluchService;
  
  @Before
  public void setUp() {
  	MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testExistAfterNoKluchs() {
  	mockUser("test", "password");
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(false));
  }

  @Test
  public void testExistsKluchs() {
  	User user = mockUser("test", "password");
  	postKluchsFor(Arrays.asList(user), 1);
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(true));
  }

  @Test
  public void testDoNotExistsKluchs() {
  	mockUser("test", "password");
    boolean exists = feedService.existsFeedAfter("test", new FeedRequest(FeedDirection.PREVIOUS, null, Optional.empty()), false);
    assertThat(exists, equalTo(false));
  }
  
  @Test
  public void getOneKluchBefore() {
  	User user = mockUser("test", "password");
  	postKluchsFor(Arrays.asList(user), 1);
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
    postKluchsFor(Arrays.asList(user), 30);
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
  	postKluchsFor(Arrays.asList(user), 1);
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
  	postKluchsFor(Arrays.asList(user), 30);
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

    postKluchsWithHashtag(hashtagText, 1);
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
    postKluchsWithHashtag(hashtagText, 30);
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
  	User user = mockUser("user");
  	postKluchsWhichMention(user, 15);
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
  	User user = mockUser("user");
  	postKluchsWhichMention(user, 30);
  	Feed<KluchFeedElement> feed = feedService.getMentions("user", new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
  	assertThat(feed.getElements(), notNullValue());
    assertThat(feed.getNext(), nullValue());
    assertThat(feed.getPrevious(), notNullValue());
    assertThat(feed.getTotalElements(), equalTo(30L));
    assertThat(feed.getElements().size(), equalTo(30));
    feed.getElements().forEach(e -> assertThat(e.isLiked(), equalTo(false)));
  }
  
  private List<Kluch> postKluchsFor(List<User> users, int number) {
  	List<Kluch> kluchs = new ArrayList<>();
  	for(int i = 0; i < number; i++) {
  		Random random = new Random();
  		User user = users.get(random.nextInt(users.size()));
  		kluchs.add(postKluch(user, generateString(50)));
  	}
  	return kluchs;
  }
  
  private List<Kluch> postKluchsWhichMention(User user, int number) {
  	List<Kluch> kluchs = new ArrayList<>();
  	for(int i = 0; i < number; i++) {
  		String kluchText = generateString(50);
  		kluchText += " @" + user.getUsername();
  		kluchs.add(postKluch(user, kluchText));
  	}
  	return kluchs;
  }
  
  private List<Kluch> postKluchsWithHashtag(String hashtag, int number) {
  	User user = mockUser(getRandomUsername());
  	List<Kluch> kluchs = new ArrayList<>();
  	for(int i = 0; i < number; i++) {
  		String kluchText = generateString(50);
  		kluchText += " #" + hashtag;
  		kluchs.add(postKluch(user, kluchText));
  	}
  	return kluchs;
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
