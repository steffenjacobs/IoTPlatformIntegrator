package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerTypeContainer;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class OpenHabTriggerTransformationAdapter {

	public TriggerTypeContainer convertTriggerTypeContainer(TriggerTypeContainer container, ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {

		// parse item first, if one exists.
		final SharedItem item;
		if (ArrayUtils.contains(container.getTriggerType().getTypeSpecificKeys(), TriggerTypeSpecificKey.ItemName)) {
			item = itemDirectory.getItemByName("" + container.getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
			container.getTriggerTypeSpecificValues().put(TriggerTypeSpecificKey.ItemName, item);
		} else {
			item = null;
		}
		for (TriggerTypeSpecificKey key : container.getTriggerType().getTypeSpecificKeys()) {
			if (key == TriggerTypeSpecificKey.ItemName) {
				continue;
			}
			final String strVal = "" + container.getTriggerTypeSpecificValues().get(key);

			// parse command as previous value if possible
			if ((key == TriggerTypeSpecificKey.State || key == TriggerTypeSpecificKey.PreviousState) && item != null) {
				final Object cmd = transformForTriggerTypeSpecificKey(TriggerTypeSpecificKey.Command, strVal, itemDirectory, commandParser);
				if (cmd != Command.Unknown) {
					container.getTriggerTypeSpecificValues().put(key, cmd);
					continue;
				}
			}
			final Object transformedValue = transformForTriggerTypeSpecificKey(key, strVal, itemDirectory, commandParser);
			container.getTriggerTypeSpecificValues().put(key, transformedValue);
		}

		return container;
	}

	private Object transformForTriggerTypeSpecificKey(TriggerTypeSpecificKey key, String value, ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {
		switch (key) {
		case Command:
			return commandParser.parseCommand(value);
		case PreviousState:
		case State:
		case Event:
		case Channel:
		case Time:
			return value;
		case ItemName:
			SharedItem i = itemDirectory.getItemByName(value);
			return i != null ? i : "?" + value;
		default:
			return value;
		}
	}

}
