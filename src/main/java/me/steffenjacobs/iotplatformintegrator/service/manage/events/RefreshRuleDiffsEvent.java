package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RefreshRuleDiffsEvent extends Event {

	public RefreshRuleDiffsEvent() {
		super(EventType.RefreshRuleDiffs);
	}

}
