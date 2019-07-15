package me.steffenjacobs.iotplatformintegrator.service.authentication;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;

/** @author Steffen Jacobs */
public interface AuthenticationService {

	void registerUser(User user);

	boolean isSignupRequired();

	boolean isLoginSuccessful();

	User getCurrentUser();

}