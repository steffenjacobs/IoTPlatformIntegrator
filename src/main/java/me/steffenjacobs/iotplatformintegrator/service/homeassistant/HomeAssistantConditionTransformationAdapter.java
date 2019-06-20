package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.ui.util.StringUtil;

/** @author Steffen Jacobs */
public class HomeAssistantConditionTransformationAdapter {
	public List<SharedCondition> parseCondition(Object o, ItemDirectory itemDirectory) {
		if (!(o instanceof Map)) {
			System.out.println(o);
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) o;
		ConditionType conditionType = parseConditionType("" + map.get("condition"));

		final List<SharedCondition> conditions = new ArrayList<>();

		switch (conditionType) {
		case ItemState:
			if (map.get("condition").equals("numeric_state")) {
				String below = "" + map.get("below");
				String above = "" + map.get("above");
				String itemName = "" + map.get("entity_id");

				if (StringUtil.isNonNull(below)) {
					// TODO: fix label + description
					Map<String, Object> conditionProperties = new HashMap<>();
					conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.SMALLER);
					conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), below);
					String description = itemName + " below " + below;
					String label = ConditionType.ItemState + " below condition";
					SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
					conditions.add(sc);
				}
				if (StringUtil.isNonNull(above)) {
					// TODO: fix label + description
					Map<String, Object> conditionProperties = new HashMap<>();
					conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.BIGGER);
					conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), above);
					String description = itemName + " above " + above;
					String label = ConditionType.ItemState + " above condition";
					SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
					conditions.add(sc);
				}
			} else if (map.get("condition").equals("state")) {
				String itemName = "" + map.get("entity_id");
				String state = "" + map.get("state");
				Map<String, Object> conditionProperties = new HashMap<>();
				conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.EQUAL);
				conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));

				Command cmd = Command.parse(state);
				final String description;
				if (cmd != Command.Unknown) {
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), state);
					description = itemName + " EQUALS " + state;
				} else {
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), cmd);
					description = itemName + " EQUALS Command" + cmd;
				}

				String label = ConditionType.ItemState + " equal condition";
				SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
				conditions.add(sc);
			}

			break;
		}
		return conditions;
	}

	private ConditionType parseConditionType(String conditionType) {
		switch (conditionType) {
		case "numeric_state":
		case "state":
			return ConditionType.ItemState;
		default:
			return ConditionType.Unknown;
		}
	}
}
