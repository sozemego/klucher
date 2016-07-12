package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.Klucher;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.KluchResult;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Klucher.class)
@WebIntegrationTest
@Transactional
@ActiveProfiles("test")
public class KluchServiceTest {
  
  @Autowired
  private KluchService kluchService;
  
  @Autowired
  private KluchDao kluchDao;
  
  @Test
  public void testTooLongKluch() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    KluchResult result = kluchService.post("author", generateString(251));
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  @Test
  public void testValidKluch() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    String kluchText = generateString(140);
    kluchService.post("author", kluchText);
    assertThat(kluchDao.count(), equalTo(1L));
  }
  
  @Test
  public void testEmptyKluch() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    KluchResult result = kluchService.post("author", "");
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  @Test
  public void testNullKluch() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    KluchResult result = kluchService.post("author", null);
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  @Test
  public void testNullAuthor() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    KluchResult result = kluchService.post(null, generateString(50));
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  @Test
  public void testEmptyAuthor() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    KluchResult result = kluchService.post("", generateString(50));
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(0L));
  }
  
  public void testAlreadyPosted() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    String kluchText = generateString(50);
    KluchResult result = kluchService.post("author", kluchText);
    assertThat(result.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(result.isSuccessful(), equalTo(false));
    assertThat(kluchDao.count(), equalTo(1L));
    kluchService.post("author", kluchText);
    assertThat(kluchDao.count(), equalTo(1L));
  }
  
  @Test
  public void testPostDifferentContent() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    String kluchText = generateString(50);
    kluchService.post("author", kluchText);
    assertThat(kluchDao.count(), equalTo(1L));
    String secondKluch = generateString(51);
    kluchService.post("author", secondKluch);
    assertThat(kluchDao.count(), equalTo(2L));
  }
  
  @Test
  public void testSameContentDifferentAuthor() throws Exception {
    assertThat(kluchDao.count(), equalTo(0L));
    String kluchText = generateString(50);
    kluchService.post("author", kluchText);
    assertThat(kluchDao.count(), equalTo(1L));
    kluchService.post("author2", kluchText);
    assertThat(kluchDao.count(), equalTo(2L));
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
