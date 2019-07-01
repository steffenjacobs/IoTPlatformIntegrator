package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.Map;
import java.util.UUID;

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
	private static final JsonTransformerHelper jsonHelper = new JsonTransformerHelper();

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
		jsonHelper.putIfNotNull(json, KEY_ID, diff.getUid().toString());
		jsonHelper.putIfNotNull(json, KEY_LABEL, diff.getLabel());
		jsonHelper.putIfNotNull(json, KEY_DESCRIPTION, diff.getDescription());
		jsonHelper.putIfNotNull(json, KEY_ELEMENT_SUBTYPE, diff.getElementType().name());
		jsonHelper.putIfNotNull(json, KEY_ELEMENT_TYPE, diff.getElementType().getType());
		if (diff.isNegative()) {
			jsonHelper.putIfNotNull(json, KEY_NEGATIVE, diff.isNegative());
		}
		jsonHelper.putIfNotNull(json, KEY_RELATIVE_ELEMENT_ID, diff.getRelativeElementId());
		jsonHelper.putStringMapIfNotNull(json, KEY_PROPERTIES_ADDED, diff.getPropertiesAdded());
		jsonHelper.putStringMapIfNotNull(json, KEY_PROPERTIES_REMOVED, diff.getPropertiesRemoved());
		jsonHelper.putStringMapIfNotNull(json, KEY_PROPERTIES_UPDATED, diff.getPropertiesUpdated());
		return json;
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
		Map<String, Object> propertiesAdded = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_ADDED);
		Map<String, Object> propertiesRemoved = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_REMOVED);
		Map<String, Object> propertiesUpdated = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_UPDATED);

		return new SharedRuleElementDiff(UUID.fromString(uid), description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated, isNegative,
				relativeElementId);
	}

}
