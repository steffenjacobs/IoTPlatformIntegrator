package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.out;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.DataType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;

/** @author Steffen Jacobs */
public class HomeAssistantReverseConditionTransformationAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantReverseConditionTransformationAdapter.class);

	public Object parseCondition(SharedCondition sc) {
		final Map<String, Object> map = new HashMap<>();
		switch (sc.getConditionTypeContainer().getConditionType()) {
		case TimeOfDay:
			map.put("condition", "time");
			map.put("before", sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.StartTime));
			map.put("after", sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.EndTime));
			break;
		case ItemState:
			final SharedItem item = (SharedItem) sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName);
			map.put("entity_id", item.getName());
			final Object state = sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.State);
			if (item.getType().getDatatype() == DataType.Numerical) {
				map.put("condition", "numeric_state");
				final Operation operator = (Operation) sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Operator);
				if (operator == Operation.SMALLER) {
					map.put("below", state);
				} else if (operator == Operation.BIGGER) {
					map.put("above", state);
				} else if (operator == Operation.EQUAL) {
					map.put("state", state);
				} else {
					LOG.error("Could not reverse transform condition {}: Invalid operaion {}.", sc.getLabel(), operator.name());
				}

			} else {
				map.put("condition", "state");
				map.put("state", state);

			}
			break;
		case DayOfWeek:
		case ScriptEvaluatesTrue:
		case Unknown:
			LOG.error("Could not reverse transform condition {}", sc.getLabel());
			break;

		}
		return map;
	}
}
