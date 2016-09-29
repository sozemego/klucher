package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.hashtag.service.HashtagService;
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

	public static final Pattern USER_MENTION_EXTRACTOR = Pattern.compile("(?:^|\\s)(@\\w+)");
	private final HashtagService hashtagService;

	@Autowired
	public KluchAssembler(HashtagService hashtagService) {
		this.hashtagService = hashtagService;
	}

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
		kluch.setHashtags(hashtagService.process(kluch));
		kluch.setMentions(new ArrayList<>(extractUserMentions(kluchText)));
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


}
