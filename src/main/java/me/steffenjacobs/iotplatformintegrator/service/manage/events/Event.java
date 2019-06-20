package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */

public class Event {
	protected final EventType eventType;

	protected Event(EventType eventType) {
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return eventType;
	}
}
