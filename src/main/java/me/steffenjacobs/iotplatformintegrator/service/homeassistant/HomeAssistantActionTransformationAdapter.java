package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class HomeAssistantActionTransformationAdapter {

	public SharedAction parseAction(Object o, ItemDirectory itemDirectory) {
		if (!(o instanceof Map)) {
			System.out.println(o);
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) o;
		ActionType actionType = parseActionType("" + map.get("action"));

		return null;
	}

	private ActionType parseActionType(String actionType) {
		switch (actionType) {
		default:
			return ActionType.Unknown;
		}
	}

}
