package me.steffenjacobs.iotplatformintegrator.service.openhab;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionTypeContainer;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class OpenHabActionTransformationAdapter {

	public ActionTypeContainer convertActionTypeContainer(ActionTypeContainer container, ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {

		// parse item first, if one exists.
		final SharedItem item;
		if (ArrayUtils.contains(container.getActionType().getTypeSpecificKeys(), ActionTypeSpecificKey.ItemName)) {
			item = itemDirectory.getItemByName("" + container.getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName));
			container.getActionTypeSpecificValues().put(ActionTypeSpecificKey.ItemName, item);
		} else {
			item = null;
		}
		for (ActionTypeSpecificKey key : container.getActionType().getTypeSpecificKeys()) {
			if (key == ActionTypeSpecificKey.ItemName) {
				continue;
			}
			final String strVal = "" + container.getActionTypeSpecificValues().get(key);

			final Object transformedValue = transformForActionTypeSpecificKey(key, strVal, itemDirectory, commandParser);
			container.getActionTypeSpecificValues().put(key, transformedValue);
		}

		return container;
	}

	private Object transformForActionTypeSpecificKey(ActionTypeSpecificKey key, String value, ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {
		switch (key) {
		case ItemName:
			SharedItem i = itemDirectory.getItemByName(value);
			return i != null ? i : "?" + value;
		case Command:
			return commandParser.parseCommand(value);
		case Enable:
		case ConsiderConditions:
			return Boolean.parseBoolean(value);
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
