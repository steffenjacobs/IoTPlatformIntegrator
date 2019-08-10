package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ClearAllRemoteItemsEvent extends Event {

	public ClearAllRemoteItemsEvent() {
		super(EventType.ClearAllRemoteItems);
	}

}
