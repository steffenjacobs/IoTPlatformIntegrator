package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;

/** @author Steffen Jacobs */
public class SharedRuleElementDiffJsonTransformer {

	private static final String KEY_LABEL = "label";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ELEMENT_TYPE = "element-type";
	private static final String KEY_NEGATIVE = "negative";
	private static final String KEY_RELATIVE_ELEMENT_ID = "rel-elem-id";

	private static final String KEY_PROPERTIES_ADDED = "added";
	private static final String KEY_PROPERTIES_REMOVED = "removed";
	private static final String KEY_PROPERTIES_UPDATED = "updated";

	public String toJSON(SharedRuleElementDiff diff) {
		final JSONObject json = new JSONObject();
		putIfNotNull(json, KEY_LABEL, diff.getLabel());
		putIfNotNull(json, KEY_DESCRIPTION, diff.getDescription());
		putIfNotNull(json, KEY_ELEMENT_TYPE, diff.getElementType());
		putIfNotNull(json, KEY_NEGATIVE, diff.isNegative());
		putIfNotNull(json, KEY_RELATIVE_ELEMENT_ID, diff.getRelativeElementId());
		putMapIfNotNull(json, KEY_PROPERTIES_ADDED, diff.getPropertiesAdded());
		putMapIfNotNull(json, KEY_PROPERTIES_REMOVED, diff.getPropertiesRemoved());
		putMapIfNotNull(json, KEY_PROPERTIES_UPDATED, diff.getPropertiesUpdated());
		return "";
	}

	private void putMapIfNotNull(JSONObject json, String key, Map<String, Object> map) {
		if (!map.isEmpty()) {
			final JSONArray jsonArr = new JSONArray();

			for (Entry<String, Object> entr : map.entrySet()) {
				final JSONObject entrJson = new JSONObject();
				entrJson.put(entr.getKey(), entr.getValue());
			}

			json.put(key, jsonArr);
		}
	}

	private void putIfNotNull(JSONObject json, String key, Object value) {
		if (value != null) {
			json.put(key, value);
		}
	}

	private Map<String, Object> readMap(JSONObject jsonArr, String key) {
		Map<String, Object> map = new HashMap<>();
		for (Object o : jsonArr.getJSONArray(key)) {
			JSONObject json = (JSONObject) o;
			for (String k : json.keySet()) {
				map.put(k, json.get(k));
			}
		}
		return map;
	}

	public SharedRuleElementDiff fromJSON(String jsonStr) {
		JSONObject json = new JSONObject(jsonStr);

		String label = json.getString(KEY_LABEL);
		String description = json.getString(KEY_DESCRIPTION);
		String elemType = json.getString(KEY_ELEMENT_TYPE);
		final SharedElementType elementType = SharedElementType.valueOf(elemType);
		Boolean isNegative = json.getBoolean(KEY_NEGATIVE);
		Integer relativeElementId = json.getInt(KEY_RELATIVE_ELEMENT_ID);
		Map<String, Object> propertiesAdded = readMap(json, KEY_PROPERTIES_ADDED);
		Map<String, Object> propertiesRemoved = readMap(json, KEY_PROPERTIES_REMOVED);
		Map<String, Object> propertiesUpdated = readMap(json, KEY_PROPERTIES_UPDATED);

		return new SharedRuleElementDiff(description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated, isNegative, relativeElementId);
	}

}
