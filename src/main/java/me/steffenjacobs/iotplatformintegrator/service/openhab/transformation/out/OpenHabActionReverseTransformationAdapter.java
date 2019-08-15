package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionTypeContainer;

/** @author Steffen Jacobs */
public class OpenHabActionReverseTransformationAdapter {

	public void convertActionTypeContainer(ActionTypeContainer container, OpenHabCommandReverseTransformer commandParser) {
		for (ActionTypeSpecificKey key : ActionTypeSpecificKey.values()) {
			container.getActionTypeSpecificValues().computeIfPresent(key, (k, v) -> transformForActionTypeSpecificKey(k, v, commandParser));
		}
	}

	private Object transformForActionTypeSpecificKey(ActionTypeSpecificKey key, Object value, OpenHabCommandReverseTransformer commandParser) {
		switch (key) {
		case ItemName:
			return ((SharedItem) value).getName();
		case Command:
			return commandParser.parseCommand((Command) value);
		case Enable:
		case ConsiderConditions:
			return Boolean.toString((Boolean) value);
		case RuleUUIDs:
		case Type:
		case Script:
		case Sink:
		case Sound:
		case Text:
		default:
			return value;
		}
	}
}
