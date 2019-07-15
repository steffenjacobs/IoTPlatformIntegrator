package me.steffenjacobs.iotplatformintegrator.service.storage.json;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;

/** @author Steffen Jacobs */
public class UserScoreJsonTransformer {

	private static final String KEY_ID = "_id";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_ADDITIONS = SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_ADDED;
	private static final String KEY_DELETIONS = SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_REMOVED;
	private static final String KEY_MODIFICATIONS = SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_UPDATED;

	public UserScore fromJSON(String jsonStr) {
		final JSONObject json = new JSONObject(jsonStr);

		String userid = getStringOrNull(json, KEY_ID);
		String username = getStringOrNull(json, KEY_USERNAME);
		long additions = getLongOrNull(json, KEY_ADDITIONS);
		long deletions = getLongOrNull(json, KEY_DELETIONS);
		long modifications = getLongOrNull(json, KEY_MODIFICATIONS);

		return new UserScore(username, UUID.fromString(userid), additions, deletions, modifications);
	}

	public String getStringOrNull(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public long getLongOrNull(JSONObject json, String key) {
		try {
			return json.getLong(key);
		} catch (JSONException e) {
			return 0;
		}
	}

}
