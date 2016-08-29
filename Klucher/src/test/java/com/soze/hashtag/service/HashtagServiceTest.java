package com.soze.hashtag.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.hashtag.dao.HashtagDao;
import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.model.Kluch;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HashtagServiceTest {

  @Autowired
  private HashtagService hashtagService;
  
  @MockBean
  private HashtagDao hashtagDao;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void testOneValidHashtag() throws Exception {
    String hashtagText = "#hashtag";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.get(0).getText(), equalTo(hashtagText));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testAnotherValidHashtag() throws Exception {
    String hashtagText = "#hashtagsuperhashtag";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.get(0).getText(), equalTo(hashtagText));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testHashtagWithUnderScore() throws Exception {
    String hashtagText = "#hashtag_superhashtag";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);  
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.get(0).getText(), equalTo(hashtagText));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testAllDigitHashtag() throws Exception {
    String hashtagText = "#12333441255533990";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.get(0).getText(), equalTo(hashtagText));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testAllWeirdCharactersHashtag() throws Exception {
    String hashtagText = "#$%%@@#$&&^%%^&&%^&&%%^&";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(0));
  }
  
  @Test
  public void testMultipleHashtags() throws Exception {
    String hashtagText = "#one #two #three";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(3));
    assertThat(hashtags.contains(new Hashtag("#one")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#two")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#three")), equalTo(true));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
    assertThat(hashtags.get(1).getKluchs().size(), equalTo(1));
    assertThat(hashtags.get(2).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testMultipleHashtagsNoSpace() throws Exception {
    String hashtagText = "#one#two#three";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.contains(new Hashtag("#one")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#two")), equalTo(false));
    assertThat(hashtags.contains(new Hashtag("#three")), equalTo(false));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testAnotherSetOfHashtags() throws Exception {
    String hashtagText = "#bla #123334444 #gleblegle";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(3));
    assertThat(hashtags.contains(new Hashtag("#bla")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#123334444")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#gleblegle")), equalTo(true));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
    assertThat(hashtags.get(1).getKluchs().size(), equalTo(1));
    assertThat(hashtags.get(2).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testJustPoundCharacter() throws Exception {
    String hashtagText = "#";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(0));
  }
  
  @Test
  public void testMultiplePoundCharacters() throws Exception {
    String hashtagText = "### # # text";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(0));
  }
  
  @Test
  public void testInvalidHashtagsBetweenPounds() throws Exception {
    String hashtagText = "##oh# # # text";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(0));
    assertThat(hashtags.contains(new Hashtag("#oh")), equalTo(false));
  }
  
  @Test
  public void testMultipleOfSameHashtag() throws Exception {
    String hashtagText = "#bla #bla";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(1));
    assertThat(hashtags.contains(new Hashtag("#bla")), equalTo(true));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testTwoMultipleOfSameHashtag() throws Exception {
    String hashtagText = "#bla #bla #eh #eh";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(2));
    assertThat(hashtags.contains(new Hashtag("#bla")), equalTo(true));
    assertThat(hashtags.contains(new Hashtag("#eh")), equalTo(true));
    assertThat(hashtags.get(0).getKluchs().size(), equalTo(1));
  }
  
  @Test
  public void testNoHashtags() throws Exception {
    String hashtagText = "";
    Kluch kluch = getKluch(hashtagText + " rest of kluch");
    when(hashtagDao.findOne(hashtagText)).thenReturn(null);
    List<Hashtag> hashtags = hashtagService.process(kluch);
    assertThat(hashtags.size(), equalTo(0));
  }

  private Kluch getKluch(String text) {
    Kluch kluch = new Kluch();
    kluch.setText(text);
    return kluch;
  }

}
