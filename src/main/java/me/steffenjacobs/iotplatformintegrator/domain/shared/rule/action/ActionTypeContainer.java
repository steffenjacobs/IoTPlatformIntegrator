package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;

/** @author Steffen Jacobs */
public class ActionTypeContainer {

	private final ActionType actionType;

	private final Map<ActionTypeSpecificKey, Object> actionTypeSpecificValues;

	public ActionTypeContainer(ActionType actionType, Map<String, Object> propertiesMap) {
		this.actionType = actionType;
		this.actionTypeSpecificValues = new HashMap<>();
		for (ActionTypeSpecificKey key : actionType.getTypeSpecificKeys()) {
			actionTypeSpecificValues.put(key, propertiesMap.get(key.getKeyString()));
		}
	}

	public ActionType getActionType() {
		return actionType;
	}

	public Map<ActionTypeSpecificKey, Object> getActionTypeSpecificValues() {
		return actionTypeSpecificValues;
	}
}
