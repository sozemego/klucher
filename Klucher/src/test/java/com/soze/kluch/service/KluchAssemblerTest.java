package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;

import com.soze.hashtag.service.HashtagService;
import com.soze.kluch.model.Kluch;

public class KluchAssemblerTest {
  
  @Test
  public void testValidKluch() throws Exception {
    HashtagService mockService = mock(HashtagService.class);
    when(mockService.process((Kluch)notNull())).thenReturn(new ArrayList<>());
    KluchAssembler assembler = new KluchAssembler(mockService);
    String kluchText = generateString(140);
    Kluch kluch = assembler.assembleKluch("author", kluchText);
    assertThat(kluch.getAuthor(), equalTo("author"));
    assertThat(kluch.getText(), equalTo(kluchText));
  }
  
  @Test
  public void testKluchWithHashtags() throws Exception {
    //Tests for hashtag extraction are in the HashtagServiceTest.java file
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
