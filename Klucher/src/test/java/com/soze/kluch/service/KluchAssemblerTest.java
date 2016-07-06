package com.soze.kluch.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.soze.kluch.exceptions.InvalidKluchContentException;
import com.soze.kluch.model.Kluch;

public class KluchAssemblerTest {

  @Test(expected = InvalidKluchContentException.class)
  public void testTooLongKluch() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    assembler.assembleKluch("author", generateString(251));
  }
  
  @Test
  public void testValidKluch() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    String kluchText = generateString(140);
    Kluch kluch = assembler.assembleKluch("author", kluchText);
    assertThat(kluch.getAuthor(), equalTo("author"));
    assertThat(kluch.getText(), equalTo(kluchText));
  }
  
  @Test(expected = InvalidKluchContentException.class)
  public void testEmptyKluch() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    String kluchText = generateString(0);
    assembler.assembleKluch("author", kluchText);
  }
  
  @Test(expected = InvalidKluchContentException.class)
  public void testNullKluch() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    assembler.assembleKluch("author", null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullAuthor() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    String kluchText = generateString(25);
    assembler.assembleKluch(null, kluchText);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAuthor() throws Exception {
    KluchAssembler assembler = new KluchAssembler();
    String kluchText = generateString(25);
    assembler.assembleKluch("", kluchText);
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
