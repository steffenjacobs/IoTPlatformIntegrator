package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedItemChangedEvent extends Event {

	private final SharedItem item;

	public SelectedItemChangedEvent(final SharedItem item) {
		super(EventType.SELECTED_ITEM_CHANGE);
		this.item = item;
	}

	public SharedItem getItem() {
		return item;
	}

}
