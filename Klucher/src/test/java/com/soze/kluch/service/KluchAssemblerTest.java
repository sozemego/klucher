package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.soze.kluch.model.Kluch;

public class KluchAssemblerTest {
  
  @Test
  public void testValidKluch() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    String kluchText = generateString(140);
    Kluch kluch = assembler.assembleKluch("author", kluchText);
    assertThat(kluch.getAuthor(), equalTo("author"));
    assertThat(kluch.getText(), equalTo(kluchText));
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
