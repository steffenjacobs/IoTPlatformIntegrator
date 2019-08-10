package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ClearAllRemoteRulesEvent extends Event {

	public ClearAllRemoteRulesEvent() {
		super(EventType.ClearAllRemoteRules);
	}

}
