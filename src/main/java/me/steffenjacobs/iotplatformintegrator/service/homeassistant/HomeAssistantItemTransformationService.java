package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.ArrayList;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.states.State;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public class HomeAssistantItemTransformationService {
	public List<SharedItem> transformItems(List<State> states) {
		List<SharedItem> items = new ArrayList<>();
		for (State state : states) {
			String name = state.getEntityId();
			String label = state.getAttributes().getFriendlyName();
			ItemType type = parseItemType(name);
			items.add(new SharedItem(name, label, type));
		}
		return items;
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
		default:
			return ItemType.Unknown;
		}
	}
}
