package com.soze.usersettings.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.InvalidUserSettingException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.user.model.User;
import com.soze.usersettings.model.UserSettings;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserSettingsServiceTest extends TestWithMockUsers {

	@Autowired
	private UserSettingsService service;
	
	@Test(expected = NullOrEmptyException.class)
	public void testGetSettingsNullUsername() {
		service.getUserSettings(null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testGetSettingsEmptyUsername() {
		service.getUserSettings("");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testGetSettingsUserDoesNotExist() {
		service.getUserSettings("user");
	}
	
	@Test
	public void testGetSettingsValid() {
		User user = mockUser("user");
		UserSettings settings = service.getUserSettings("user");
		assertThat(settings, equalTo(user.getUserSettings()));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testSaveSettingsNullUsername() {
		service.saveSettings(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testSaveSettingsEmptyUsername() {
		service.saveSettings("", null);
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testSaveSettingsUserDoesNotExist() {
		service.saveSettings("user", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testSaveSettingsNullSettings() {
		mockUser("user");
		service.saveSettings("user", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testSaveSettingsInvalidSettings() {
		mockUser("user");
		service.saveSettings("user", new UserSettings(null, null, null));
	}
	
	@Test(expected = InvalidUserSettingException.class)
	public void testSaveSettingsInvalidSettingsInvalidKluchsPerRequestLessThan() {
		mockUser("user");
		service.saveSettings("user", new UserSettings(null, 5, "valid description"));
	}
	
	@Test(expected = InvalidUserSettingException.class)
	public void testSaveSettingsInvalidSettingsInvalidKluchsPerRequestGreaterThan() {
		mockUser("user");
		service.saveSettings("user", new UserSettings(null, 195, "valid description"));
	}
	
	@Test(expected = InvalidUserSettingException.class)
	public void testSaveSettingsInvalidSettingsInvalidProfileDescriptionTooLong() {
		mockUser("user");
		service.saveSettings("user", new UserSettings(null, 60, generateText(165)));
	}
	
	@Test
	public void testSaveSettingsValid() {
		User user = mockUser("user");
		UserSettings settings = service.saveSettings("user", new UserSettings(null, 60, "valid description"));
		assertThat(settings.getAvatarPath(), equalTo(user.getUserSettings().getAvatarPath()));
		assertThat(settings.getKluchsPerRequest(), equalTo(60));
		assertThat(settings.getProfileDescription(), equalTo("valid description"));
	}
	
	@Test
	public void testDeleteValidUser() {
		User user = mockUser("user");
		service.deleteUser("user");
		assertThat(user.isDeleted(), equalTo(true));
	}
	
	private String generateText(int length) {
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++) {
			sb.append("c");
		}
		return sb.toString();
	}
	
}
