package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.ui.util.StringUtil;

/** @author Steffen Jacobs */
public class HomeAssistantConditionTransformationAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantConditionTransformationAdapter.class);

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
			} else if (map.get("condition").equals("sun")) {
				String before = "" + map.get("before");
				String after = "" + map.get("after");
				SharedItem item = itemDirectory.getItemByName("sun.sun");
				handleAfterBefore(map, conditions, before, after, item);
			} else if (map.get("condition").equals("zone")) {

				// TODO: fix handling of zones
				SharedItem item = itemDirectory.getItemByName("" + map.get("entity_id"));
				String zone = "" + map.get("zone");
				Map<String, Object> conditionProperties = new HashMap<>();
				conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), zone);
				conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.EQUAL);
				conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), item);
				// TODO: fix label + description
				String label = ConditionType.ItemState + " equal condition for zone";
				String description = String.format("%s is in zone %s", item.getLabel(), zone);

				conditions.add(new SharedCondition(ConditionType.ItemState, conditionProperties, description, label));
			} else {
				LOG.error("invalid condition for condition type item state.");
			}
			break;
		case TimeOfDay:
			String before = "" + map.get("before");
			String after = "" + map.get("after");
			// TODO: fix label + description
			Map<String, Object> conditionProperties = new HashMap<>();
			conditionProperties.put(ConditionTypeSpecificKey.StartTime.getKeyString(), before);
			conditionProperties.put(ConditionTypeSpecificKey.EndTime.getKeyString(), after);

			String description = String.format("Time is between %s and %s", before, after);
			String label = ConditionType.TimeOfDay + " condition";
			SharedCondition sc = new SharedCondition(ConditionType.TimeOfDay, conditionProperties, description, label);
			conditions.add(sc);
			break;
		default:
			LOG.error("Could not parse condition {}", conditionType);
			break;
		}
		return conditions;
	}

	private void handleAfterBefore(Map<String, Object> map, final List<SharedCondition> conditions, String before, String after, SharedItem item) {
		if (StringUtil.isNonNull(before)) {
			// TODO: fix label + description
			Map<String, Object> conditionProperties = new HashMap<>();
			conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.SMALLER);
			conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), item);

			if (map.containsKey("before_offset")) {
				int beforeOffset = Integer.parseInt("" + map.get("before_offset"));
				conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), before + (beforeOffset > 0 ? "+" + beforeOffset : beforeOffset));
			}

			String description = item.getLabel() + " before " + before;
			String label = ConditionType.ItemState + " before condition";
			SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
			conditions.add(sc);
		}
		if (StringUtil.isNonNull(after)) {
			// TODO: fix label + description
			Map<String, Object> conditionProperties = new HashMap<>();
			conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.BIGGER);
			conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), item);

			if (map.containsKey("after_offset")) {
				int afterOffset = Integer.parseInt("" + map.get("after_offset"));
				conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), after + (afterOffset > 0 ? "+" + afterOffset : afterOffset));
			}
			String description = item.getLabel() + " after " + after;
			String label = ConditionType.ItemState + " after condition";
			SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
			conditions.add(sc);
		}
	}

	private ConditionType parseConditionType(String conditionType) {
		switch (conditionType) {
		case "numeric_state":
		case "state":
		case "sun":
		case "zone":
			return ConditionType.ItemState;
		case "time":
			return ConditionType.TimeOfDay;
		default:
			return ConditionType.Unknown;
		}
	}
}
