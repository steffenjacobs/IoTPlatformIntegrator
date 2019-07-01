package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
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
}
