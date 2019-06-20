package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class TestEvent extends Event {

	public TestEvent() {
		super(EventType.Test);
	}

}
