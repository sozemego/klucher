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

  private final int beforeKluchsPerRequest = 30;
  private final int afterKluchsPerRequest = 30;
  
  @Autowired
  private FeedConstructor constructor;
  @Autowired
  private KluchDao kluchDao;

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUser() {
    constructor.constructFeed("doesNotExist", 0, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUserEmptyName() {
    constructor.constructFeed("", 0, false);
  }

  @Test
  public void testNoKluchs() {
    addUserToDb("test", "password");
    Feed feed = constructor.constructFeed("test", 0, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testLessThanPageKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(), equalTo(25));
  }

  @Test
  public void testKluchsExistButNotBeforeTimestamp() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", 0, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testPaginatedKluchsGetOnce() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(beforeKluchsPerRequest));
  }

  @Test
  public void testPaginatedKluchsGetTwice() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeed("test", Long.MAX_VALUE, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(beforeKluchsPerRequest));
    long oldestTimestamp = getOldestTimestamp(kluchs);
    Feed secondFeed = constructor.constructFeed("test", oldestTimestamp, false);
    Page<Kluch> secondKluchs = secondFeed.getKluchs();
    assertThat(secondKluchs.getSize(),
        equalTo(beforeKluchsPerRequest));
    assertThat(secondKluchs.getTotalElements(),
        equalTo(kluchsToAdd - beforeKluchsPerRequest));
    assertThat(secondKluchs.getNumberOfElements(),
        equalTo((int) kluchsToAdd - beforeKluchsPerRequest));
  }

  @Test
  public void testLessThanPageAfterKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(afterKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(), equalTo(25));
  }

  @Test
  public void testKluchsExistButNotAfterTimestamp() {
    addUserToDb("test", "password");
    long kluchsToAdd = 25;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", Long.MAX_VALUE, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(afterKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(0L));
    assertThat(kluchs.getNumberOfElements(), equalTo(0));
  }

  @Test
  public void testPaginatedAfterKluchsGetOnce() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(afterKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(afterKluchsPerRequest));
  }

  @Test
  public void testPaginatedAfterKluchsGetTwice() {
    addUserToDb("test", "password");
    long kluchsToAdd = 35;
    addKluchsForUser("test", kluchsToAdd);
    Feed feed = constructor.constructFeedAfter("test", 0, false);
    Page<Kluch> kluchs = feed.getKluchs();
    assertThat(kluchs.getSize(),
        equalTo(afterKluchsPerRequest));
    assertThat(kluchs.getTotalElements(), equalTo(kluchsToAdd));
    assertThat(kluchs.getNumberOfElements(),
        equalTo(afterKluchsPerRequest));
    long earliestTimestamp = getEarliestTimestamp(kluchs);
    Feed secondFeed = constructor.constructFeedAfter("test", earliestTimestamp, false);
    Page<Kluch> secondKluchs = secondFeed.getKluchs();
    assertThat(secondKluchs.getSize(),
        equalTo(afterKluchsPerRequest));
    assertThat(secondKluchs.getTotalElements(),
        equalTo(kluchsToAdd - afterKluchsPerRequest));
    assertThat(secondKluchs.getNumberOfElements(),
        equalTo((int) kluchsToAdd - afterKluchsPerRequest));
  }

  @Test
  public void testExistAfterNoKluchs() {
    addUserToDb("test", "password");
    boolean exists = constructor.existsFeedAfter("test", 0, false);
    assertThat(exists, equalTo(false));
  }

  @Test
  public void testExistsKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 120;
    addKluchsForUser("test", kluchsToAdd);
    boolean exists = constructor.existsFeedAfter("test", 0, false);
    assertThat(exists, equalTo(true));
  }

  @Test
  public void testDoNotExistsKluchs() {
    addUserToDb("test", "password");
    long kluchsToAdd = 120;
    addKluchsForUser("test", kluchsToAdd);
    boolean exists = constructor.existsFeedAfter("test", Long.MAX_VALUE, false);
    assertThat(exists, equalTo(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUsername() {
    constructor.constructFeed(null, 0, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonExistedUsername() {
    constructor.constructFeed("iDontExist", 0, false);
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
