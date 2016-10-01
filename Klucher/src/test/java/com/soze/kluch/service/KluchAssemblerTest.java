package com.soze.kluch.service;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.soze.kluch.model.Kluch;
import com.soze.user.model.User;

public class KluchAssemblerTest {

	private final KluchAssembler assembler = new KluchAssembler();

	@Test
	public void testValidKluch() throws Exception {
		String kluchText = "some text, perfectly ok.";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testOneValidHashtag() throws Exception {
		String kluchText = "#hashtag";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("hashtag"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAnotherValidHashtag() throws Exception {
		String kluchText = "#hashtagsuperhashtag";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("hashtagsuperhashtag"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testHashtagWithUnderScore() throws Exception {
		String kluchText = "#hashtag_superhashtag";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("hashtag_superhashtag"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAllDigitHashtag() throws Exception {
		String kluchText = "#12333441255533990";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("12333441255533990"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAllWeirdCharactersHashtag() throws Exception {
		String kluchText = "#$%%@@#$&&^%%^&&%^&&%%^&";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getHashtags().contains("$%%@@#$&&^%%^&&%^&&%%^&"), equalTo(false));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleHashtags() throws Exception {
		String kluchText = "#one #two #three";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(3));
		assertThat(kluch.getHashtags().contains("one"), equalTo(true));
		assertThat(kluch.getHashtags().contains("two"), equalTo(true));
		assertThat(kluch.getHashtags().contains("three"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleHashtagsNoSpace() throws Exception {
		String kluchText = "#one#two#three";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("one"), equalTo(true));
		assertThat(kluch.getHashtags().contains("two"), equalTo(false));
		assertThat(kluch.getHashtags().contains("three"), equalTo(false));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAnotherSetOfHashtags() throws Exception {
		String kluchText = "#bla #123334444 #gleblegle";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(3));
		assertThat(kluch.getHashtags().contains("bla"), equalTo(true));
		assertThat(kluch.getHashtags().contains("123334444"), equalTo(true));
		assertThat(kluch.getHashtags().contains("gleblegle"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testJustPoundCharacter() throws Exception {
		String kluchText = "#";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultiplePoundCharacters() throws Exception {
		String kluchText = "### # # text";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testInvalidHashtagsBetweenPounds() throws Exception {
		String kluchText = "##oh# # # text";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleOfSameHashtag() throws Exception {
		String kluchText = "#bla #bla";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(1));
		assertThat(kluch.getHashtags().contains("bla"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testTwoMultipleOfSameHashtag() throws Exception {
		String kluchText = "#bla #bla #eh #eh";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(2));
		assertThat(kluch.getHashtags().contains("bla"), equalTo(true));
		assertThat(kluch.getHashtags().contains("eh"), equalTo(true));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testOneValidMention() throws Exception {
		String kluchText = "@user";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(1));
		assertThat(kluch.getMentions().contains("user"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMentionWithUnderscore() throws Exception {
		String kluchText = "@user_name";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(1));
		assertThat(kluch.getMentions().contains("user_name"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAllDigitUsername() throws Exception {
		String kluchText = "@12333441255533990";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(1));
		assertThat(kluch.getMentions().contains("12333441255533990"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testAllWeirdCharactersMention() throws Exception {
		String kluchText = "@$%%@@#$&&^%%^&&%^&&%%^&";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getMentions().contains("$%%@@#$&&^%%^&&%^&&%%^&"), equalTo(false));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleMentions() throws Exception {
		String kluchText = "@one @two @three";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(3));
		assertThat(kluch.getMentions().contains("one"), equalTo(true));
		assertThat(kluch.getMentions().contains("two"), equalTo(true));
		assertThat(kluch.getMentions().contains("three"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleMentionsNoSpace() throws Exception {
		String kluchText = "@one@two@three";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(1));
		assertThat(kluch.getMentions().contains("one"), equalTo(true));
		assertThat(kluch.getMentions().contains("two"), equalTo(false));
		assertThat(kluch.getMentions().contains("three"), equalTo(false));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testJustAtCharacter() throws Exception {
		String kluchText = "@";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleAtCharacters() throws Exception {
		String kluchText = "@@@ @ @ text";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testInvalidUsernameBetweenAts() throws Exception {
		String kluchText = "@@oh@ @ @ text";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(0));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testMultipleOfSameMention() throws Exception {
		String kluchText = "@user @user";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(1));
		assertThat(kluch.getMentions().contains("user"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

	@Test
	public void testTwoMultipleOfSameMention() throws Exception {
		String kluchText = "@user @user @eh @eh";
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		Kluch kluch = assembler.assembleKluch(user, kluchText);
		assertThat(kluch.getAuthorId(), equalTo(1L));
		assertThat(kluch.getText(), equalTo(kluchText));
		assertThat(kluch.getHashtags().size(), equalTo(0));
		assertThat(kluch.getMentions().size(), equalTo(2));
		assertThat(kluch.getMentions().contains("user"), equalTo(true));
		assertThat(kluch.getMentions().contains("eh"), equalTo(true));
		assertThat(kluch.getTimestamp(), notNullValue());
	}

}
