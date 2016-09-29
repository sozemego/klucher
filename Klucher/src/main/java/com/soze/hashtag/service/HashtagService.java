package com.soze.hashtag.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;

/**
 * A service which extracts hashtags from kluchs' content and saves a set of
 * hashtags in {@link Kluch} entity. Hashtags are extracted using the following
 * regular expression: "(?:^|\\s)(#\\w+)". Hashtags are case-insensitive.
 * 
 * @author sozek
 *
 */
@Service
public class HashtagService {

	private static final Set<String> EMPTY_LIST = new HashSet<>(0);
	private final Pattern hashtagExtractor = Pattern.compile("(?:^|\\s)(#\\w+)");

	/**
	 * Extracts hashtags from a given Kluch and saves a list of hashtags in this
	 * kluch.
	 * 
	 * @param kluch
	 * @return set of extracted hashtags
	 */
	public Set<String> process(Kluch kluch) {
		Set<String> hashtagsTexts = extractHashtags(kluch.getText());
		if (hashtagsTexts.isEmpty()) {
			return EMPTY_LIST;
		}
		return getHashtags(hashtagsTexts, kluch);
	}

	/**
	 * Matches regions of text which are hashtags and returns them as a Set.
	 * 
	 * @param kluchText
	 * @return
	 */
	private Set<String> extractHashtags(String kluchText) {
		Matcher matcher = hashtagExtractor.matcher(kluchText);
		Set<String> hashtags = new HashSet<>();
		while (matcher.find()) {
			String hashtag = matcher.group(1);
			hashtags.add(hashtag.substring(1));
		}
		return hashtags;
	}

	/**
	 * Returns lower-case hashtags and adds all hashtags to a given kluch.
	 * 
	 * @param hashtagTexts
	 * @param kluch
	 * @return set of lower-case hashtags
	 */
	private Set<String> getHashtags(Set<String> hashtagTexts, Kluch kluch) {
		Set<String> hashtags = new HashSet<>();
		for (String hashtag : hashtagTexts) {
			hashtags.add(saveHashtagInKluch(hashtag.toLowerCase(), kluch));
		}
		return hashtags;
	}

	/**
	 * Assembles a single {@link Hashtag} from text.
	 * 
	 * @param hashtagText
	 * @param kluch
	 * @return
	 */
	private String saveHashtagInKluch(String hashtagText, Kluch kluch) {
		kluch.getHashtags().add(hashtagText);
		return hashtagText;
	}

}
