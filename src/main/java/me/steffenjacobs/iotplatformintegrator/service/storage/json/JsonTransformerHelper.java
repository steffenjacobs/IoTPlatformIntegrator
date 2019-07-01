package me.steffenjacobs.iotplatformintegrator.service.storage.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

/** @author Steffen Jacobs */
public class JsonTransformerHelper {

	public void putIfNotNull(JSONObject json, String key, Object value) {
		if (value != null) {
			json.put(key, value);
		}
	}

	public void putMapIfNotNull(JSONObject json, String key, Map<? extends SharedTypeSpecificKey, Object> map) {
		if (!map.isEmpty()) {
			final JSONArray jsonArr = new JSONArray();

			for (Entry<? extends SharedTypeSpecificKey, Object> entr : map.entrySet()) {
				final JSONObject entrJson = new JSONObject();
				entrJson.put(entr.getKey().getKeyString(), entr.getValue());
				jsonArr.put(entrJson);
			}

			json.put(key, jsonArr);
		}
	}

	public void putStringMapIfNotNull(JSONObject json, String key, Map<String, Object> map) {
		if (!map.isEmpty()) {
			final JSONArray jsonArr = new JSONArray();

			for (Entry<String, Object> entr : map.entrySet()) {
				final JSONObject entrJson = new JSONObject();
				entrJson.put(entr.getKey(), entr.getValue());
				jsonArr.put(entrJson);
			}

			json.put(key, jsonArr);
		}
	}

	public Map<String, Object> readMapFromJson(JSONObject jsonArr, String key) {
		Map<String, Object> map = new HashMap<>();
		for (Object o : getArrayOrEmpty(jsonArr, key)) {
			JSONObject json = (JSONObject) o;
			for (String k : json.keySet()) {
				map.put(k, json.get(k));
			}
		}
		return map;
	}

	private JSONArray getArrayOrEmpty(JSONObject json, String key) {
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			return new JSONArray();
		}
	}
}
