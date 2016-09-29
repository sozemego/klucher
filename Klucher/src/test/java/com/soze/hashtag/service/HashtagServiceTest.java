package com.soze.hashtag.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.kluch.model.Kluch;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HashtagServiceTest {

	@Autowired
	private HashtagService hashtagService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOneValidHashtag() throws Exception {
		String hashtagText = "#hashtag";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains(hashtagText.substring(1)), equalTo(true));
	}

	@Test
	public void testAnotherValidHashtag() throws Exception {
		String hashtagText = "#hashtagsuperhashtag";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains(hashtagText.substring(1)), equalTo(true));
	}

	@Test
	public void testHashtagWithUnderScore() throws Exception {
		String hashtagText = "#hashtag_superhashtag";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains(hashtagText.substring(1)), equalTo(true));
	}

	@Test
	public void testAllDigitHashtag() throws Exception {
		String hashtagText = "#12333441255533990";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains(hashtagText.substring(1)), equalTo(true));
	}

	@Test
	public void testAllWeirdCharactersHashtag() throws Exception {
		String hashtagText = "#$%%@@#$&&^%%^&&%^&&%%^&";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(0));
		assertThat(hashtags.contains(hashtagText.substring(1)), equalTo(false));
	}

	@Test
	public void testMultipleHashtags() throws Exception {
		String hashtagText = "#one #two #three";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(3));
		assertThat(hashtags.contains("one"), equalTo(true));
		assertThat(hashtags.contains("two"), equalTo(true));
		assertThat(hashtags.contains("three"), equalTo(true));
	}

	@Test
	public void testMultipleHashtagsNoSpace() throws Exception {
		String hashtagText = "#one#two#three";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains("one"), equalTo(true));
		assertThat(hashtags.contains("two"), equalTo(false));
		assertThat(hashtags.contains("three"), equalTo(false));
	}

	@Test
	public void testAnotherSetOfHashtags() throws Exception {
		String hashtagText = "#bla #123334444 #gleblegle";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(3));
		assertThat(hashtags.contains("bla"), equalTo(true));
		assertThat(hashtags.contains("123334444"), equalTo(true));
		assertThat(hashtags.contains("gleblegle"), equalTo(true));
	}

	@Test
	public void testJustPoundCharacter() throws Exception {
		String hashtagText = "#";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(0));
	}

	@Test
	public void testMultiplePoundCharacters() throws Exception {
		String hashtagText = "### # # text";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(0));
	}

	@Test
	public void testInvalidHashtagsBetweenPounds() throws Exception {
		String hashtagText = "##oh# # # text";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(0));
		assertThat(hashtags.contains("oh"), equalTo(false));
	}

	@Test
	public void testMultipleOfSameHashtag() throws Exception {
		String hashtagText = "#bla #bla";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(1));
		assertThat(hashtags.contains("bla"), equalTo(true));
	}

	@Test
	public void testTwoMultipleOfSameHashtag() throws Exception {
		String hashtagText = "#bla #bla #eh #eh";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(2));
		assertThat(hashtags.contains("bla"), equalTo(true));
		assertThat(hashtags.contains("eh"), equalTo(true));
	}

	@Test
	public void testNoHashtags() throws Exception {
		String hashtagText = "";
		Kluch kluch = getKluch(hashtagText + " rest of kluch");
		Set<String> hashtags = hashtagService.process(kluch);
		assertThat(hashtags.size(), equalTo(0));
	}

	private Kluch getKluch(String text) {
		Kluch kluch = new Kluch();
		kluch.setText(text);
		return kluch;
	}

}
