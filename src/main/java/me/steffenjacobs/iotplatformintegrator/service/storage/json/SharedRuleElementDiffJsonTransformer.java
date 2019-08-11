package me.steffenjacobs.iotplatformintegrator.service.storage.json;

import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
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

	public static final String KEY_CREATOR = "creator";
	private static final String KEY_ASSOCIATED_RULE_NAME = "rule";
	private static final String KEY_ID = "_id";
	private static final String KEY_LABEL = "label";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ELEMENT_SUBTYPE = "subtype";
	private static final String KEY_ELEMENT_TYPE = "type";
	private static final String KEY_NEGATIVE = "negative";
	private static final String KEY_RELATIVE_ELEMENT_ID = "rel-elem-id";
	private static final String KEY_TARGET_RULE_NAME = "target-rule-name";
	private static final String KEY_SOURCE_RULE_NAME = "source-rule-name";
	private static final String KEY_PREV_DIFF_ID = "prev-diff-id";

	public static final String KEY_PROPERTIES_ADDED = "added";
	public static final String KEY_PROPERTIES_REMOVED = "removed";
	public static final String KEY_PROPERTIES_UPDATED = "updated";

	public JSONObject toJSON(SharedRuleElementDiff diff, String associatedRuleName, String creatorName) {
		final JSONObject json = new JSONObject();
		jsonHelper.putIfNotNull(json, KEY_CREATOR, creatorName);
		jsonHelper.putIfNotNull(json, KEY_ASSOCIATED_RULE_NAME, associatedRuleName);
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
		diff.getTargetRule().ifPresent(r -> jsonHelper.putIfNotNull(json, KEY_TARGET_RULE_NAME, r));
		diff.getPrevDiff().ifPresent(r -> jsonHelper.putIfNotNull(json, KEY_PREV_DIFF_ID, r.getUid()));
		diff.getSourceRule().ifPresent(r -> jsonHelper.putIfNotNull(json, KEY_SOURCE_RULE_NAME, r.getName()));
		return json;
	}

	public RuleDiffParts fromJSON(String jsonStr) {
		JSONObject json = new JSONObject(jsonStr);

		String uid = getStringOrNull(json, KEY_ID);
		String label = getStringOrNull(json, KEY_LABEL);
		String description = getStringOrNull(json, KEY_DESCRIPTION);
		String type = getStringOrNull(json, KEY_ELEMENT_TYPE);

		final SharedElementType elementType;
		if (type.equals(SharedElementType.ACTION_TYPE)) {
			elementType = ActionType.valueOf(getStringOrNull(json, KEY_ELEMENT_SUBTYPE));

		} else if (type.contentEquals(SharedElementType.CONDITION_TYPE)) {
			elementType = ConditionType.valueOf(getStringOrNull(json, KEY_ELEMENT_SUBTYPE));

		} else if (type.equals(SharedElementType.TRIGGER_TYPE)) {
			elementType = TriggerType.valueOf(getStringOrNull(json, KEY_ELEMENT_SUBTYPE));

		} else {
			elementType = UnknownSharedElementType.INSTANCE;
			LOG.error("Invalid element sub type: {}", type);
		}

		Boolean isNegative = getBooleanOrNull(json, KEY_NEGATIVE);
		Integer relativeElementId = getIntOrNull(json, KEY_RELATIVE_ELEMENT_ID);
		Map<String, Object> propertiesAdded = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_ADDED);
		Map<String, Object> propertiesRemoved = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_REMOVED);
		Map<String, Object> propertiesUpdated = jsonHelper.readMapFromJson(json, KEY_PROPERTIES_UPDATED);

		String creator = getStringOrNull(json, KEY_CREATOR);

		SharedRuleElementDiff ruleDiff = new SharedRuleElementDiff(UUID.fromString(uid), description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated,
				isNegative, relativeElementId);

		String targetRuleName = getStringOrNull(json, KEY_TARGET_RULE_NAME);
		String sourceRuleName = getStringOrNull(json, KEY_SOURCE_RULE_NAME);
		String previousDiffId = getStringOrNull(json, KEY_PREV_DIFF_ID);
		return new RuleDiffParts(ruleDiff, creator, previousDiffId, targetRuleName, sourceRuleName);
	}

	public int getIntOrNull(JSONObject json, String key) {
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			return 0;
		}
	}

	public boolean getBooleanOrNull(JSONObject json, String key) {
		try {
			return json.getBoolean(key);
		} catch (JSONException e) {
			return false;
		}
	}

	public String getStringOrNull(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public static class RuleDiffParts {
		private final SharedRuleElementDiff ruleDiff;
		private final String creator;
		private final String prevDiffId;
		private final String targetRuleName;
		private final String sourceRuleName;

		public RuleDiffParts(SharedRuleElementDiff ruleDiff, String creator, String prevDiffId, String targetRuleName, String sourceRuleName) {
			super();
			this.ruleDiff = ruleDiff;
			this.creator = creator;
			this.prevDiffId = prevDiffId;
			this.targetRuleName = targetRuleName;
			this.sourceRuleName = sourceRuleName;
		}

		public String getCreator() {
			return creator;
		}

		public String getPrevDiffId() {
			return prevDiffId;
		}

		public SharedRuleElementDiff getRuleDiff() {
			return ruleDiff;
		}

		public String getTargetRuleName() {
			return targetRuleName;
		}

		public String getSourceRuleName() {
			return sourceRuleName;
		}
	}

}
