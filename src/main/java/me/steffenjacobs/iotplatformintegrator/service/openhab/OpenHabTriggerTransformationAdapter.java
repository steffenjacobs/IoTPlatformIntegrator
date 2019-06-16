package me.steffenjacobs.iotplatformintegrator.service.openhab;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerTypeContainer;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class OpenHabTriggerTransformationAdapter {

	private final ItemDirectory itemDirectory;

	public OpenHabTriggerTransformationAdapter(ItemDirectory itemDirectory) {
		this.itemDirectory = itemDirectory;

	}

	public TriggerTypeContainer convertTriggerTypeContainer(TriggerTypeContainer container) {

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
				final Object cmd = transformForTriggerTypeSpecificKey(TriggerTypeSpecificKey.Command, strVal);
				if (cmd != Command.Unknown) {
					container.getTriggerTypeSpecificValues().put(key, cmd);
					continue;
				}
			}
			final Object transformedValue = transformForTriggerTypeSpecificKey(key, strVal);
			container.getTriggerTypeSpecificValues().put(key, transformedValue);
		}

		return container;
	}

	private Object transformForTriggerTypeSpecificKey(TriggerTypeSpecificKey key, String value) {
		switch (key) {
		case Command:
			return parseCommand(value);
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

	private ItemType.Command parseCommand(String command) {
		switch (command) {
		case "ON":
			return Command.On;
		case "OFF":
			return Command.Off;
		case "OPEN":
			return Command.Open;
		case "CLOSE":
			return Command.Closed;
		case "STRING":
			return Command.String;
		case "DECIMAL":
			return Command.Decimal;
		case "INCREASE":
			return Command.Increase;
		case "DECREASE":
			return Command.Decrease;
		case "PERCENT":
			return Command.Percent;
		case "HSB":
			return Command.HSB;
		case "POINT":
			return Command.Point;
		case "PLAY":
			return Command.Play;
		case "PAUSE":
			return Command.Pause;
		case "NEXT":
			return Command.Next;
		case "PREVIOUS":
			return Command.Previous;
		case "REWIND":
			return Command.Rewind;
		case "FASTFORWARD":
			return Command.Fastforward;
		case "UP":
			return Command.Up;
		case "DOWN":
			return Command.Down;
		case "STOPMOVE":
			return Command.StopMove;
		default:
			return Command.Unknown;
		}
	}
}
