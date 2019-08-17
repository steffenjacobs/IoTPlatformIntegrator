package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.out;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;

/** @author Steffen Jacobs */
public class HomeAssistantActionReverseTransformationAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantActionReverseTransformationAdapter.class);

	public Object parseAction(SharedAction action) {
		final Map<String, Object> map = new HashMap<>();

		if (action.getActionTypeContainer().getActionType() != ActionType.ItemCommand) {
			LOG.error("Could not reverse parse action {}: Unsupported action type {}.", action.getLabel(), action.getActionTypeContainer().getActionType().name());
			return "";
		}
		final SharedItem item = (SharedItem) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName);
		map.put("entity_id", item.getName());
		final Command command = (Command) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Command);
		map.put("service", item.getType().name().toLowerCase() + "." + command.name().toLowerCase());
		return map;
	}

}
