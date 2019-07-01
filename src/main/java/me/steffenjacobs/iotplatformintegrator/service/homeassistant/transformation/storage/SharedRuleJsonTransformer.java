package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;

/** @author Steffen Jacobs */
@SuppressWarnings("unused")
public class SharedRuleJsonTransformer {
	private static final Logger LOG = LoggerFactory.getLogger(SharedRuleJsonTransformer.class);
	private static final JsonTransformerHelper jsonHelper = new JsonTransformerHelper();

	private static final String KEY_NAME = "name";
	private static final String KEY_ID = "_id";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_STATUS = "status";
	private static final String KEY_VISIBLE = "visible";
	private static final String KEY_TRIGGERS = "triggers";

	private static final String KEY_RULE_ELEMENT_LABEL = "label";
	private static final String KEY_RULE_ELEMENT_DESCRIPTION = "description";
	private static final String KEY_RULE_ELEMENT_ELEMENT_ID = "element-id";

	private static final String KEY_RULE_ELEMENT_TYPE = "type";
	private static final String KEY_RULE_ELEMENT_CONTAINER = "container";

	public JSONObject toJson(SharedRule rule) {
		JSONObject json = new JSONObject();
		jsonHelper.putIfNotNull(json, KEY_NAME, rule.getName());
		jsonHelper.putIfNotNull(json, KEY_ID, rule.getId());
		jsonHelper.putIfNotNull(json, KEY_DESCRIPTION, rule.getDescription());
		jsonHelper.putIfNotNull(json, KEY_STATUS, rule.getStatus());
		jsonHelper.putIfNotNull(json, KEY_VISIBLE, rule.getVisible());

		putTriggerSetIfNotNull(json, KEY_TRIGGERS, rule.getTriggers());
		return json;
	}

	private void putTriggerSetIfNotNull(JSONObject json, String key, Set<? extends SharedRuleElement> set) {
		JSONArray jsonArr = new JSONArray();
		for (SharedRuleElement e : set) {
			JSONObject jsonElem = new JSONObject();
			jsonElem.put(KEY_RULE_ELEMENT_LABEL, e.getLabel());
			jsonElem.put(KEY_RULE_ELEMENT_DESCRIPTION, e.getDescription());
			jsonElem.put(KEY_RULE_ELEMENT_ELEMENT_ID, e.getRelativeElementId());
			if (e instanceof SharedTrigger) {
				serializeTriggerSpecific(jsonElem, (SharedTrigger) e);
			} else if (e instanceof SharedCondition) {
				serializeConditionSpecific(jsonElem, (SharedCondition) e);
			} else if (e instanceof SharedAction) {
				serializeActionSpecific(jsonElem, (SharedAction) e);
			} else {
				LOG.error("Invalid shared element type: {}", e);
			}
			jsonArr.put(jsonElem);
		}

		if (!jsonArr.isEmpty()) {
			json.put(key, jsonArr);
		}
	}

	private JSONObject serializeTriggerSpecific(JSONObject json, SharedTrigger trigger) {
		json.put(KEY_RULE_ELEMENT_TYPE, trigger.getTriggerTypeContainer().getTriggerType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues());
		return json;
	}

	private JSONObject serializeConditionSpecific(JSONObject json, SharedCondition condition) {
		json.put(KEY_RULE_ELEMENT_TYPE, condition.getConditionTypeContainer().getConditionType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, condition.getConditionTypeContainer().getConditionTypeSpecificValues());
		return json;
	}

	private JSONObject serializeActionSpecific(JSONObject json, SharedAction action) {
		json.put(KEY_RULE_ELEMENT_TYPE, action.getActionTypeContainer().getActionType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, action.getActionTypeContainer().getActionTypeSpecificValues());
		return json;
	}
}
