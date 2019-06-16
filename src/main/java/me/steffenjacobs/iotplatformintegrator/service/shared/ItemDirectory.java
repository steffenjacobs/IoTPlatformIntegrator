package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public class ItemDirectory {

	private final Map<String, SharedItem> items = new HashMap<>();

	public SharedItem getItemByName(String name) {
		return items.get(name);
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
	
	public Iterable<SharedItem> getAllItems(){
		return items.values();
	}
}
