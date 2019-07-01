package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.UnknownSharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;

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
	private static final String KEY_CONDITIONS = "conditions";
	private static final String KEY_ACTIONS = "actions";

	private static final String KEY_RULE_ELEMENT_LABEL = "label";
	private static final String KEY_RULE_ELEMENT_DESCRIPTION = "description";
	private static final String KEY_RULE_ELEMENT_ELEMENT_ID = "element-id";

	private static final String KEY_RULE_ELEMENT_TYPE = "type";
	private static final String KEY_RULE_ELEMENT_SUBTYPE = "subtype";
	private static final String KEY_RULE_ELEMENT_CONTAINER = "container";

	public SharedRule fromJson(String jsonStr) {
		JSONObject json = new JSONObject(jsonStr);
		String name = json.getString(KEY_NAME);
		UUID uuid = UUID.fromString(json.getString(KEY_ID));
		String description = json.getString(KEY_DESCRIPTION);
		String status = json.getString(KEY_STATUS);
		String visible = json.getString(KEY_VISIBLE);

		JSONArray jsonTriggers = json.getJSONArray(KEY_TRIGGERS);

		return null;
	}

	private Triple<Set<SharedTrigger>, Set<SharedCondition>, Set<SharedAction>> parseTriggers(JSONArray jsonArr) {
		Set<SharedTrigger> triggers = new HashSet<>();
		Set<SharedCondition> conditions = new HashSet<>();
		Set<SharedAction> actions = new HashSet<>();
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject json = jsonArr.getJSONObject(i);
			String label = json.getString(KEY_RULE_ELEMENT_LABEL);
			String description = json.getString(KEY_RULE_ELEMENT_DESCRIPTION);
			int elementId = Integer.parseInt(json.getString(KEY_RULE_ELEMENT_ELEMENT_ID));

			final Map<String, Object> properties = readMap(json, KEY_RULE_ELEMENT_CONTAINER);

			String subType = json.getString(KEY_RULE_ELEMENT_SUBTYPE);

			if (subType.equals(SharedElementType.ACTION_TYPE)) {
				ActionType actionType = ActionType.valueOf(json.getString(KEY_RULE_ELEMENT_TYPE));
				actions.add(new SharedAction(actionType, properties, description, label, elementId));

			} else if (subType.contentEquals(SharedElementType.CONDITION_TYPE)) {
				ConditionType conditionType = ConditionType.valueOf(json.getString(KEY_RULE_ELEMENT_TYPE));
				conditions.add(new SharedCondition(conditionType, properties, description, label, elementId));

			} else if (subType.equals(SharedElementType.TRIGGER_TYPE)) {
				TriggerType triggerType = TriggerType.valueOf(json.getString(KEY_RULE_ELEMENT_TYPE));
				triggers.add(new SharedTrigger(triggerType, properties, description, label, elementId));

			} else {
				LOG.error("Invalid element sub type: {}", subType);
			}

		}
		return Triple.of(triggers, conditions, actions);
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

	public JSONObject toJson(SharedRule rule) {
		JSONObject json = new JSONObject();
		jsonHelper.putIfNotNull(json, KEY_NAME, rule.getName());
		jsonHelper.putIfNotNull(json, KEY_ID, rule.getId());
		jsonHelper.putIfNotNull(json, KEY_DESCRIPTION, rule.getDescription());
		jsonHelper.putIfNotNull(json, KEY_STATUS, rule.getStatus());
		jsonHelper.putIfNotNull(json, KEY_VISIBLE, rule.getVisible());

		putElementSetIfNotNull(json, KEY_TRIGGERS, rule.getTriggers());
		putElementSetIfNotNull(json, KEY_CONDITIONS, rule.getConditions());
		putElementSetIfNotNull(json, KEY_ACTIONS, rule.getActions());
		return json;
	}

	private void putElementSetIfNotNull(JSONObject json, String key, Set<? extends SharedRuleElement> set) {
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
		json.put(KEY_RULE_ELEMENT_TYPE, SharedElementType.TRIGGER_TYPE);
		json.put(KEY_RULE_ELEMENT_SUBTYPE, trigger.getTriggerTypeContainer().getTriggerType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues());
		return json;
	}

	private JSONObject serializeConditionSpecific(JSONObject json, SharedCondition condition) {
		json.put(KEY_RULE_ELEMENT_TYPE, SharedElementType.CONDITION_TYPE);
		json.put(KEY_RULE_ELEMENT_SUBTYPE, condition.getConditionTypeContainer().getConditionType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, condition.getConditionTypeContainer().getConditionTypeSpecificValues());
		return json;
	}

	private JSONObject serializeActionSpecific(JSONObject json, SharedAction action) {
		json.put(KEY_RULE_ELEMENT_TYPE, SharedElementType.ACTION_TYPE);
		json.put(KEY_RULE_ELEMENT_SUBTYPE, action.getActionTypeContainer().getActionType());
		jsonHelper.putMapIfNotNull(json, KEY_RULE_ELEMENT_CONTAINER, action.getActionTypeContainer().getActionTypeSpecificValues());
		return json;
	}
}