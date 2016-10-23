package com.soze.usersettings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.InvalidUserSettingException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.usersettings.model.UserSettings;

@Service
public class UserSettingsService {

	private final UserDao userDao;
	
	@Autowired
	public UserSettingsService(UserDao userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * Changes user settings (only if there were any changes). Otherwise does nothing.
	 * This method also validates the settings.
	 * @param username
	 * @param userSettings
	 * @return user's current user settings
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 * @throws InvalidUserSettingException
	 */
	public UserSettings saveSettings(String username, UserSettings userSettings) throws NullOrEmptyException, UserDoesNotExistException, InvalidUserSettingException {
		User user = getUser(username);
		if(userSettings == null) {
			throw new NullOrEmptyException("User settings");
		}
		userSettings.setAvatarPath(user.getUserSettings().getAvatarPath());
		validateUserSettings(userSettings);
		if(settingsChanged(user, userSettings)) {
			user.setUserSettings(userSettings);
			userDao.save(user);
		}
		return userSettings;
	}
	
	/**
	 * Returns a copy of user settings for a given user.
	 * @param username
	 * @return
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 */
	public UserSettings getUserSettings(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		return copyUserSettings(user.getUserSettings());
	}
	
	/**
	 * Sets this user to be deleted.
	 * @param username
	 * @return
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 */
	public boolean deleteUser(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.setDeleted(true);
		userDao.save(user);
		SecurityContextHolder.getContext().setAuthentication(null);
		return true;
	}
	
	private UserSettings copyUserSettings(UserSettings userSettings) {
		UserSettings userSettingsCopy = new UserSettings(
				userSettings.getAvatarPath(), 
				userSettings.getKluchsPerRequest(), 
				userSettings.getProfileDescription());
		return userSettingsCopy;
	}
	
	private void validateUserSettings(UserSettings userSettings) throws InvalidUserSettingException, NullOrEmptyException {
		Integer kluchsPerRequest = userSettings.getKluchsPerRequest();
		if(kluchsPerRequest != null) {
			if(kluchsPerRequest < 10 || kluchsPerRequest > 120) {
				throw new InvalidUserSettingException("Kluchs per request.");
			}
		} else {
			throw new NullOrEmptyException("Kluchs per request");
		}
		String profileDescription = userSettings.getProfileDescription();
		if(profileDescription != null) {
			if(profileDescription.length() > 140) {
				throw new InvalidUserSettingException("Profile description length.");
			}
		} else {
			throw new NullOrEmptyException("Profile description");
		}
	}
	
	private boolean settingsChanged(User user, UserSettings userSettings) {
		UserSettings currentSettings = user.getUserSettings();
		if(!userSettings.getKluchsPerRequest().equals(currentSettings.getKluchsPerRequest())) {
			return true;
		}
		if(!userSettings.getProfileDescription().equals(currentSettings.getProfileDescription())) {
			return true;
		}
		return false;
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
