package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RefreshOpenHABDataEvent extends Event {

	public RefreshOpenHABDataEvent() {
		super(EventType.REFRESH_OPENHAB_DATA);
	}

}
