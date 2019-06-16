package me.steffenjacobs.iotplatformintegrator.service.openhab;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionTypeContainer;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class OpenHabActionTransformationAdapter {

	private final ItemDirectory itemDirectory;
	private final OpenHabCommandParser commandParser;

	public OpenHabActionTransformationAdapter(ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {
		this.itemDirectory = itemDirectory;
		this.commandParser = commandParser;

	}

	public ActionTypeContainer convertActionTypeContainer(ActionTypeContainer container) {

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

			final Object transformedValue = transformForActionTypeSpecificKey(key, strVal);
			container.getActionTypeSpecificValues().put(key, transformedValue);
		}

		return container;
	}

	private Object transformForActionTypeSpecificKey(ActionTypeSpecificKey key, String value) {
		switch (key) {
		case ItemName:
			SharedItem i = itemDirectory.getItemByName(value);
			return i != null ? i : "?" + value;
		case Command:
			return commandParser.parseCommand(value);
		case Enable:
		case RuleUUIDs:
		case Type:
		case Script:
		case Sink:
		case Sound:
		case ConsiderConditions:
		case Text:
		default:
			return value;
		}
	}
}
