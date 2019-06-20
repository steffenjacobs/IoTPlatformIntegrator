package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public class ItemDirectory {

	private final Map<String, SharedItem> items = new HashMap<>();

	private static final SharedItem UNKNOWN_ITEM = new SharedItem("<unknown-item>", "Unknown Item", ItemType.Unknown);

	public SharedItem getItemByName(String name) {
		SharedItem item = items.get(name);
		return item == null ? UNKNOWN_ITEM : item;
	}

	public void addItems(Iterable<SharedItem> addedItems) {
		for (SharedItem item : addedItems) {
			addItem(item);
		}
	}

	public void addItem(SharedItem item) {
		items.put(item.getName(), item);
	}

	public void clearItems() {
		items.clear();
	}

	public Iterable<SharedItem> getAllItems() {
		return items.values();
	}
}
