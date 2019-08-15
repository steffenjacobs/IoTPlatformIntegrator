package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionTypeContainer;

/** @author Steffen Jacobs */
public class OpenHabConditionReverseTransformationAdapter {

	public void convertConditionTypeContainer(ConditionTypeContainer container) {
		for (ConditionTypeSpecificKey key : ConditionTypeSpecificKey.values()) {
			container.getConditionTypeSpecificValues().computeIfPresent(key, (k, v) -> transformForConditionTypeSpecificKey(k, v));
		}
	}

	private String transformForConditionTypeSpecificKey(ConditionTypeSpecificKey key, Object value) {
		switch (key) {
		case ItemName:
			return ((SharedItem) value).getName();
		case Operator:
			return Operation.toExportString((Operation) value);
		case Type:
		case Script:
		case StartTime:
		case EndTime:
		case State:
		default:
			return value.toString();
		}
	}
}
