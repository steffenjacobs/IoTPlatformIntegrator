package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectTargetRuleEvent extends Event {

	public SelectTargetRuleEvent() {
		super(EventType.SelectTargetRule);
	}

}
