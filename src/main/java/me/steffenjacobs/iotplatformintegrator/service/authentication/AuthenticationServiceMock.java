package me.steffenjacobs.iotplatformintegrator.service.authentication;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;

/** @author Steffen Jacobs */
public class AuthenticationServiceMock implements AuthenticationService {

	@Override
	public void registerUser(User user) {
		// nothing to do
	}

	@Override
	public boolean isSignupRequired() {
		return false;
	}

	@Override
	public boolean isLoginSuccessful() {
		return true;
	}

	@Override
	public User getCurrentUser() {
		return new User(UUID.randomUUID(), "admin", "nobodywilleverknowthispassword");
	}

}
