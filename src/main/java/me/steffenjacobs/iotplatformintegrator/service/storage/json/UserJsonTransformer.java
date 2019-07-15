package me.steffenjacobs.iotplatformintegrator.service.storage.json;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;

/** @author Steffen Jacobs */
public class UserJsonTransformer {

	private static final JsonTransformerHelper jsonHelper = new JsonTransformerHelper();

	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "label";
	private static final String KEY_PASSWORD = "description";

	public JSONObject toJSON(User user) {
		final JSONObject json = new JSONObject();
		jsonHelper.putIfNotNull(json, KEY_ID, user.getUserId().toString());
		jsonHelper.putIfNotNull(json, KEY_NAME, user.getUsername());
		jsonHelper.putIfNotNull(json, KEY_PASSWORD, user.getPassword());
		return json;
	}

	public User fromJSON(String jsonStr) {
		final JSONObject json = new JSONObject(jsonStr);

		String uid = getStringOrNull(json, KEY_ID);
		String username = getStringOrNull(json, KEY_NAME);
		String password = getStringOrNull(json, KEY_PASSWORD);
		return new User(UUID.fromString(uid), username, password);
	}

	public String getStringOrNull(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

}
