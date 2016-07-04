package com.soze.feed.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.Klucher;
import com.soze.TestWithUserBase;
import com.soze.feed.model.Feed;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@WebIntegrationTest
@Transactional
@ActiveProfiles("test")
public class FeedConstructorTest extends TestWithUserBase {

  @Autowired
  private FeedConstructor constructor;
  @Autowired
  private KluchDao kluchDao;

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUser() {
    constructor.constructFeed("doesNotExist", 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUserEmptyName() {
    constructor.constructFeed("", 0);
  }

  @Test
  public void testNoKluchs() {
    addUserToDb("test", "password");
    Feed feed = constructor.constructFeed("test", 0);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testLessThanPageKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(), equalTo(25));
  }

  @Test
  public void testKluchsExistButNotBeforeTimestamp() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", 0);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testPaginatedKluchsGetOnce() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
  }

  @Test
  public void testPaginatedKluchsGetTwice() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    long oldestTimestamp = getOldestTimestamp(kluchs);
    Feed secondFeed = constructor.constructFeed("test", oldestTimestamp);
    Page<Kluch> secondKluchs = secondFeed.getKluchs();
    assertThat(secondKluchs.getSize(),
        equalTo(FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(secondKluchs.getTotalElements(),
        equalTo(kluchsToAdd - FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
    assertThat(secondKluchs.getNumberOfElements(),
        equalTo((int) kluchsToAdd - FeedConstructor.BEFORE_KLUCHS_PER_REQUEST));
  }

  @Test
  public void testLessThanPageAfterKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(), equalTo(25));
  }

  @Test
  public void testKluchsExistButNotAfterTimestamp() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", Long.MAX_VALUE);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testPaginatedAfterKluchsGetOnce() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
  }

  @Test
  public void testPaginatedAfterKluchsGetTwice() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    long earliestTimestamp = getEarliestTimestamp(kluchs);
    Feed secondFeed = constructor.constructFeedAfter("test", earliestTimestamp);
    Page<Kluch> secondKluchs = secondFeed.getKluchs();
    assertThat(secondKluchs.getSize(),
        equalTo(FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(secondKluchs.getTotalElements(),
        equalTo(kluchsToAdd - FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
    assertThat(secondKluchs.getNumberOfElements(),
        equalTo((int) kluchsToAdd - FeedConstructor.AFTER_KLUCHS_PER_REQUEST));
  }

  @Test
  public void testExistAfterNoKluchs() {
    addUserToDb("test", "password");
    boolean exists = constructor.existsFeedAfter("test", 0);
    assertThat(exists, equalTo(false));
  }

  @Test
  public void testExistsKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 120;
    addKluchsForUser("test", kluchsToAdd);
    boolean exists = constructor.existsFeedAfter("test", 0);
    assertThat(exists, equalTo(true));
  }

  @Test
  public void testDoNotExistsKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 120;
    addKluchsForUser("test", kluchsToAdd);
    boolean exists = constructor.existsFeedAfter("test", Long.MAX_VALUE);
    assertThat(exists, equalTo(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUsername() {
    constructor.constructFeed(null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonExistedUsername() {
    constructor.constructFeed("iDontExist", 0);
  }

  private void addKluchsForUser(String username, long kluchsToAdd) {
    for (int i = 0; i < kluchsToAdd; i++) {
      kluchDao.save(createRandomKluchFor(username));
    }
  }

  private Kluch createRandomKluchFor(String username) {
    Kluch kluch = new Kluch();
    kluch.setAuthor(username);
    kluch.setText("kluch_text");
    kluch.setTimestamp(new Timestamp(System.nanoTime()));
    return kluch;
  }

  private long getOldestTimestamp(Page<Kluch> page) {
    List<Kluch> kluchs = page.getContent();
    return page.getContent().get(kluchs.size() - 1).getTimestamp().getTime();
  }

  private long getEarliestTimestamp(Page<Kluch> page) {
    return getOldestTimestamp(page);
  }

}
