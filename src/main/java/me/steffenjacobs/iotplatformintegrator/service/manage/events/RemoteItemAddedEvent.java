package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RemoteItemAddedEvent extends Event {

	private final SharedItem item;

	public RemoteItemAddedEvent(SharedItem item) {
		super(EventType.RemoteItemAdded);
		this.item = item;
	}

	public SharedItem getItem() {
		return item;
	}

}
