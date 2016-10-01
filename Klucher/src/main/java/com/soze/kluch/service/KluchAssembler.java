package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;
import com.soze.user.model.User;

/**
 * Assembles a {@link Kluch} objects.
 * 
 * @author sozek
 *
 */
@Service
public class KluchAssembler {

	private static final Pattern USER_MENTION_EXTRACTOR = Pattern.compile("(?:^|\\s)(@\\w+)");
	private static final Pattern HASHTAG_EXTRACTOR = Pattern.compile("(?:^|\\s)(#\\w+)");

	/**
	 * Assembles and returns a Kluch object. No validation is done here.
	 * 
	 * @param author
	 * @param kluchText
	 * @return a <code>Kluch</code> entity, with all fields required for DB
	 *         persistence
	 */
	public Kluch assembleKluch(User author, String kluchText) {
		Kluch kluch = new Kluch();
		kluch.setAuthorId(author.getId());
		kluch.setText(kluchText);
		kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
		kluch.setHashtags(extractHashtags(kluchText));
		kluch.setMentions(extractUserMentions(kluchText));
		return kluch;
	}

	private Set<String> extractUserMentions(String kluchText) {
		Matcher matcher = USER_MENTION_EXTRACTOR.matcher(kluchText);
		Set<String> mentions = new HashSet<>();
		while (matcher.find()) {
			String mention = matcher.group(1);
			// at sign is used to extract a mention, but we need only the username
			mentions.add(mention.substring(1));
		}
		return mentions;
	}

	/**
	 * Matches regions of text which are hashtags and returns them as a Set.
	 * 
	 * @param kluchText
	 * @return
	 */
	private Set<String> extractHashtags(String kluchText) {
		Matcher matcher = HASHTAG_EXTRACTOR.matcher(kluchText);
		Set<String> hashtags = new HashSet<>();
		while (matcher.find()) {
			String hashtag = matcher.group(1);
			hashtags.add(hashtag.substring(1).toLowerCase());
		}
		return hashtags;
	}

}
