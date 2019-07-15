package me.steffenjacobs.iotplatformintegrator.service.authentication;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class AuthenticationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);
	private final SettingService settingService;

	public AuthenticationService(SettingService settingService) {
		this.settingService = settingService;
	}

	public void registerUser(User user) {
		App.getMongoDbUserStorageService().storeUser(user);
	}

	public boolean isSignupRequired() {
		return SettingKey.USERID.getDefaultValue().equals(settingService.getSetting(SettingKey.USERID));
	}

	public boolean isLoginSuccessful() {
		CompletableFuture<Boolean> loginSuccessful = new CompletableFuture<>();
		App.getMongoDbUserStorageService().isUserValid(loadUser(), loginSuccessful);
		try {
			return loginSuccessful.get(10000, TimeUnit.SECONDS);
		} catch (ExecutionException | InterruptedException | TimeoutException e) {
			LOG.error("Login request timed out.");
			return false;
		}
	}

	private User loadUser() {
		UUID userid;
		try {
			userid = UUID.fromString(settingService.getSetting(SettingKey.USERID));
		} catch (IllegalArgumentException e) {
			userid = null;
		}
		return new User(userid, settingService.getSetting(SettingKey.USERNAME), settingService.getSetting(SettingKey.PASSWORD));
	}

}
