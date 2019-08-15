package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerTypeContainer;

/** @author Steffen Jacobs */
public class OpenHabTriggerReverseTransformationAdapter {

	public TriggerTypeContainer convertTriggerTypeContainer(TriggerTypeContainer container, OpenHabCommandReverseTransformer commandParser) {
		for (TriggerTypeSpecificKey key : TriggerTypeSpecificKey.values()) {
			container.getTriggerTypeSpecificValues().computeIfPresent(key, (k, v) -> transformForTriggerTypeSpecificKey(k, v, commandParser));
		}
		return container;
	}

	private Object transformForTriggerTypeSpecificKey(TriggerTypeSpecificKey key, Object value, OpenHabCommandReverseTransformer commandParser) {
		switch (key) {
		case Command:
			return commandParser.parseCommand((Command) value);
		case PreviousState:
		case State:
		case Event:
		case Channel:
		case Time:
			return value;
		case ItemName:
			return ((SharedItem) value).getName();
		default:
			return value;
		}
	}

}
