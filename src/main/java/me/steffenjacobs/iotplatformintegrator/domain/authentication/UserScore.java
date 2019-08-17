package me.steffenjacobs.iotplatformintegrator.domain.authentication;

import java.util.UUID;

/** @author Steffen Jacobs */
public class UserScore {

	private final String username;
	private final UUID userid;
	private final long additions;
	private final long deletions;
	private final long modifications;

	public UserScore(String username, UUID userid, long additions, long deletions, long modifications) {
		super();
		this.username = username;
		this.userid = userid;
		this.additions = additions;
		this.deletions = deletions;
		this.modifications = modifications;
	}

	public String getUsername() {
		return username;
	}

	public UUID getUserId() {
		return userid;
	}

	public long getAdditions() {
		return additions;
	}

	public long getDeletions() {
		return deletions;
	}

	public long getModifications() {
		return modifications;
	}

}
