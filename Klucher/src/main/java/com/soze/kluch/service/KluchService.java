package com.soze.kluch.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.InvalidLengthException.Adjective;
import com.soze.common.exceptions.InvalidOwnerException;
import com.soze.common.exceptions.KluchDoesNotExistException;
import com.soze.common.exceptions.KluchPreviouslyPostedException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * A service responsible for posting new Kluchs. It validates them, assembles
 * them and in the future will extract hashtag information and send it off to
 * another service (work in progress). It stores previous Kluch(s) created by an
 * user (to prevent spam). Includes rate-limiting functionality.
 * 
 * @author sozek
 *
 */
@Service
@Transactional
public class KluchService {

	private static final Logger log = LoggerFactory.getLogger(KluchService.class);
	private static final int KLUCH_MAX_LENGTH = 250;
	private final KluchDao kluchDao;
	private final UserDao userDao;
	private final KluchAssembler kluchAssembler;
	private final Map<String, String> pastKluchs = new ConcurrentHashMap<>();

	@Autowired
	public KluchService(KluchDao kluchDao, KluchAssembler kluchAssembler, UserDao userDao) {
		this.kluchDao = kluchDao;
		this.kluchAssembler = kluchAssembler;
		this.userDao = userDao;
	}

	/**
	 * Attempts to post a Kluch with given content for a user with given username.
	 * 
	 * @param username
	 *          author of given Kluch, cannot be null or empty
	 * @param kluchText
	 *          Kluch content, cannot be null or empty
	 * @throws KluchPreviouslyPostedException
	 *           if the last <code>kluchText</code> posted by this user is
	 *           identical to this kluchText
	 * @throws NullOrEmptyException
	 *           if either <code>username</code> or <code>kluchText</code> are
	 *           null or empty
	 * @throws InvalidLengthException
	 *           if <code>kluchText</code> is longer than allowed
	 * @throws UserDoesNotExistException
	 *           if user named username does not exist
	 */
	public Kluch post(String username, String kluchText)
			throws KluchPreviouslyPostedException, NullOrEmptyException, InvalidLengthException, UserDoesNotExistException {
		validateInput(username, kluchText);
		checkAlreadyPosted(username, kluchText);
		User user = getUser(username);
		Kluch kluch = kluchAssembler.assembleKluch(user, kluchText);
		kluch = kluchDao.save(kluch);
		saveLastKluch(username, kluchText);
		log.info("User [{}] successfuly posted a Kluch with text [{}].", username, kluchText);
		return kluch;
	}

	private void checkAlreadyPosted(String username, String kluchText) throws KluchPreviouslyPostedException {
		String pastKluch = pastKluchs.get(username);
		if (kluchText.equals(pastKluch)) {
			throw new KluchPreviouslyPostedException(username);
		}
	}

	private void validateInput(String username, String kluchText) throws NullOrEmptyException {
		validateUsername(username);
		validateKluch(kluchText);
	}

	private void validateUsername(String username) throws NullOrEmptyException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
	}

	private void validateKluch(String kluchText) throws NullOrEmptyException, InvalidLengthException {
		if (kluchText == null || kluchText.isEmpty()) {
			throw new NullOrEmptyException("Kluch content");
		}
		if (kluchText.length() > KLUCH_MAX_LENGTH) {
			throw new InvalidLengthException("Kluch", Adjective.LONG);
		}
	}

	private void saveLastKluch(String username, String kluchText) {
		pastKluchs.put(username, kluchText);
	}

	/**
	 * Removes {@link Kluch} with given id, if the username is an author of said
	 * kluch.
	 * 
	 * @param username
	 *          owner of kluch
	 * @param id
	 *          id of kluch
	 * @return deleted Kluch
	 * @throws NullOrEmptyException
	 *           username is empty or null
	 * @throws UserDoesNotExistException
	 *           user with username does not exist
	 * @throws InvalidOwnerException
	 *           user is not the owner of kluch with given id
	 * @throws KluchDoesNotExistException
	 *           kluch with given id does not exist
	 */
	public Kluch deleteKluch(String username, long id)
			throws NullOrEmptyException, UserDoesNotExistException, InvalidOwnerException, KluchDoesNotExistException {
		User user = getUser(username);
		Kluch kluch = kluchDao.findOne(id);
		if (kluch == null) {
			throw new KluchDoesNotExistException();
		}
		if (!user.getId().equals(kluch.getAuthorId())) {
			throw new InvalidOwnerException("Kluch");
		}
		kluchDao.delete(kluch);
		return kluch;
	}

	/**
	 * Validates username and checks if user exists. If it does, returns the
	 * {@link User}.
	 * 
	 * @param username
	 * @return
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException
	 *           if username with given <code>username</code> doesn't exist
	 */
	private User getUser(String username) throws UserDoesNotExistException, NullOrEmptyException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException("There is no user named " + username);
		}
		return user;
	}

}
