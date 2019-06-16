package me.steffenjacobs.iotplatformintegrator.service.openhab;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionTypeContainer;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;

/** @author Steffen Jacobs */
public class OpenHabConditionTransformationAdapter {

	private final ItemDirectory itemDirectory;

	public OpenHabConditionTransformationAdapter(ItemDirectory itemDirectory) {
		this.itemDirectory = itemDirectory;

	}

	public ConditionTypeContainer convertConditionTypeContainer(ConditionTypeContainer container) {

		// parse item first, if one exists.
		final SharedItem item;
		if (ArrayUtils.contains(container.getConditionType().getTypeSpecificKeys(), ConditionTypeSpecificKey.ItemName)) {
			item = itemDirectory.getItemByName("" + container.getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName));
			container.getConditionTypeSpecificValues().put(ConditionTypeSpecificKey.ItemName, item);
		} else {
			item = null;
		}
		for (ConditionTypeSpecificKey key : container.getConditionType().getTypeSpecificKeys()) {
			if (key == ConditionTypeSpecificKey.ItemName) {
				continue;
			}
			final String strVal = "" + container.getConditionTypeSpecificValues().get(key);

			final Object transformedValue = transformForConditionTypeSpecificKey(key, strVal);
			container.getConditionTypeSpecificValues().put(key, transformedValue);
		}

		return container;
	}

	private Object transformForConditionTypeSpecificKey(ConditionTypeSpecificKey key, String value) {
		switch (key) {
		case ItemName:
			SharedItem i = itemDirectory.getItemByName(value);
			return i != null ? i : "?" + value;
		case Operator:
			return Operation.fromString(value);
		case Type:
		case Script:
		case StartTime:
		case EndTime:
		case State:
		default:
			return value;
		}
	}
}
