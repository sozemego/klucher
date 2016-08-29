package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class KluchServiceTest {
  
  @MockBean
  private KluchDao kluchDao;
  
  @Autowired
  @InjectMocks
  private KluchService kluchService;
 
  
  @Test(expected = IllegalArgumentException.class)
  public void testTooLongKluch() throws Exception {
    kluchService.post("author", generateString(251));
  }
  
  @Test
  public void testValidKluch() throws Exception {
    String kluchText = generateString(140);
    String author = "author";
    Kluch kluch = getKluch(author, kluchText);
    when(kluchDao.save(kluch)).thenReturn(kluch);
    Kluch firstKluch = kluchService.post(author, kluchText);
    assertThat(firstKluch, notNullValue());
    assertThat(firstKluch.getAuthor(), equalTo(author));
    assertThat(firstKluch.getText(), equalTo(kluchText));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyKluch() throws Exception {
    kluchService.post("author", "");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullKluch() throws Exception {
    kluchService.post("author", null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullAuthor() throws Exception {
    kluchService.post(null, generateString(50));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAuthor() throws Exception {
    kluchService.post("", generateString(50));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testAlreadyPosted() throws Exception {
    String kluchText = generateString(50);
    kluchService.post("author", kluchText);
    kluchService.post("author", kluchText);
  }
  
  @Test
  public void testPostDifferentContent() throws Exception {
    String kluchText = generateString(50);
    String author = "author";
    Kluch kluch = getKluch(author, kluchText);
    when(kluchDao.save(kluch)).thenReturn(kluch);
    Kluch firstKluch = kluchService.post(author, kluchText);
    assertThat(firstKluch, notNullValue());
    assertThat(firstKluch.getAuthor(), equalTo(author));
    assertThat(firstKluch.getText(), equalTo(kluchText));
    String secondKluchText = generateString(51);
    kluch = getKluch(author, secondKluchText);
    when(kluchDao.save(kluch)).thenReturn(kluch);
    Kluch anotherKluch = kluchService.post(author, secondKluchText);
    assertThat(anotherKluch, notNullValue());
    assertThat(anotherKluch.getAuthor(), equalTo(author));
    assertThat(anotherKluch.getText(), equalTo(secondKluchText));
  }
  
  @Test
  public void testSameContentDifferentAuthor() throws Exception {
    String kluchText = generateString(50);
    String author = "author";
    Kluch kluch = getKluch(author, kluchText);
    when(kluchDao.save(kluch)).thenReturn(kluch);
    Kluch firstKluch = kluchService.post(author, kluchText);
    assertThat(firstKluch, notNullValue());
    assertThat(firstKluch.getAuthor(), equalTo(author));
    assertThat(firstKluch.getText(), equalTo(kluchText));
    String anotherAuthor = "author2";
    kluch = getKluch(anotherAuthor, kluchText);
    when(kluchDao.save(kluch)).thenReturn(kluch);
    Kluch anotherKluch = kluchService.post(anotherAuthor, kluchText);
    assertThat(anotherKluch, notNullValue());
    assertThat(anotherKluch.getAuthor(), equalTo(anotherAuthor));
    assertThat(anotherKluch.getText(), equalTo(kluchText));
  }
  
  private Kluch getKluch(String author, String text) {
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(text);
    return kluch;
  }
  
  private String generateString(int length) {
    if(length == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
      sb.append("c");
    }
    return sb.toString();
  }

}
