package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.UnknownSharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;

/** @author Steffen Jacobs */
public class SharedRuleElementDiffJsonTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(SharedRuleElementDiffJsonTransformer.class);

	private static final String KEY_ID = "_id";
	private static final String KEY_LABEL = "label";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ELEMENT_SUBTYPE = "subtype";
	private static final String KEY_ELEMENT_TYPE = "type";
	private static final String KEY_NEGATIVE = "negative";
	private static final String KEY_RELATIVE_ELEMENT_ID = "rel-elem-id";

	private static final String KEY_PROPERTIES_ADDED = "added";
	private static final String KEY_PROPERTIES_REMOVED = "removed";
	private static final String KEY_PROPERTIES_UPDATED = "updated";

	public JSONObject toJSON(SharedRuleElementDiff diff) {
		final JSONObject json = new JSONObject();
		putIfNotNull(json, KEY_ID, diff.getUid().toString());
		putIfNotNull(json, KEY_LABEL, diff.getLabel());
		putIfNotNull(json, KEY_DESCRIPTION, diff.getDescription());
		putIfNotNull(json, KEY_ELEMENT_SUBTYPE, diff.getElementType().name());
		putIfNotNull(json, KEY_ELEMENT_TYPE, diff.getElementType().getType());
		if (diff.isNegative()) {
			putIfNotNull(json, KEY_NEGATIVE, diff.isNegative());
		}
		putIfNotNull(json, KEY_RELATIVE_ELEMENT_ID, diff.getRelativeElementId());
		putMapIfNotNull(json, KEY_PROPERTIES_ADDED, diff.getPropertiesAdded());
		putMapIfNotNull(json, KEY_PROPERTIES_REMOVED, diff.getPropertiesRemoved());
		putMapIfNotNull(json, KEY_PROPERTIES_UPDATED, diff.getPropertiesUpdated());
		return json;
	}

	private void putMapIfNotNull(JSONObject json, String key, Map<String, Object> map) {
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

		String uid = json.getString(KEY_ID);
		String label = json.getString(KEY_LABEL);
		String description = json.getString(KEY_DESCRIPTION);
		String subType = json.getString(KEY_ELEMENT_SUBTYPE);

		final SharedElementType elementType;
		if (subType.equals(SharedElementType.ACTION_TYPE)) {
			elementType = ActionType.valueOf(json.getString(KEY_ELEMENT_TYPE));

		} else if (subType.contentEquals(SharedElementType.CONDITION_TYPE)) {
			elementType = ConditionType.valueOf(json.getString(KEY_ELEMENT_TYPE));

		} else if (subType.equals(SharedElementType.TRIGGER_TYPE)) {
			elementType = TriggerType.valueOf(json.getString(KEY_ELEMENT_TYPE));

		} else {
			elementType = UnknownSharedElementType.INSTANCE;
			LOG.error("Invalid element sub type: {}", subType);
		}

		Boolean isNegative = json.getBoolean(KEY_NEGATIVE);
		Integer relativeElementId = json.getInt(KEY_RELATIVE_ELEMENT_ID);
		Map<String, Object> propertiesAdded = readMap(json, KEY_PROPERTIES_ADDED);
		Map<String, Object> propertiesRemoved = readMap(json, KEY_PROPERTIES_REMOVED);
		Map<String, Object> propertiesUpdated = readMap(json, KEY_PROPERTIES_UPDATED);

		return new SharedRuleElementDiff(UUID.fromString(uid), description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated, isNegative,
				relativeElementId);
	}

}
