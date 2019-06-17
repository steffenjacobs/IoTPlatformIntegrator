package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.states.State;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;

/** @author Steffen Jacobs */
public class HomeAssistantItemTransformationService {
	public Pair<List<SharedItem>, List<SharedRule>> transformItemsAndRules(List<State> states) {
		List<SharedItem> items = new ArrayList<>();
		List<SharedRule> rules = new ArrayList<>();
		for (State state : states) {
			String name = state.getEntityId();
			ItemType type = parseItemType(name);
			if (type == ItemType.AUTOMATION_RULE) {
				rules.add(parseRule(state));
			} else {
				String label = state.getAttributes().getFriendlyName();
				items.add(new SharedItem(name, label, type));
			}
		}
		return Pair.of(items, rules);
	}

	private ItemType parseItemType(String itemString) {
		itemString = itemString.substring(0, itemString.indexOf('.'));
		switch (itemString) {
		case "sensor":
		case "sun":
			return ItemType.String;
		case "switch":
			return ItemType.Switch;
		case "zone":
		case "group":
			return ItemType.Group;
		case "automation":
			return ItemType.AUTOMATION_RULE;
		default:
			return ItemType.Unknown;
		}
	}

	private SharedRule parseRule(State state) {
		String name = state.getAttributes().getFriendlyName();
		String id = state.getContext().getId();
		String description = state.getAttributes().getFriendlyName();
		String visible = null;
		String status = state.getState();
		Set<SharedTrigger> triggers = new HashSet<>();
		Set<SharedCondition> conditions = new HashSet<>();
		Set<SharedAction> actions = new HashSet<>();
		return new SharedRule(name, id, description, visible, status, triggers, conditions, actions);
	}
}
