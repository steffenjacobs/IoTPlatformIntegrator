package me.steffenjacobs.iotplatformintegrator.domain.authentication;

import java.util.UUID;

/** @author Steffen Jacobs */
public class User {

	private final UUID userId;
	private final String username;
	private final String password;

	public User(UUID userId, String username, String password) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

}
