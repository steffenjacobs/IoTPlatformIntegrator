package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleDiffAddedEvent extends Event{

	private final SharedRuleElementDiff diffElement;
	private final String creator;

	public RuleDiffAddedEvent(SharedRuleElementDiff diffElement, String creator) {
		super(EventType.RuleDiffAdded);
		this.diffElement = diffElement;
		this.creator = creator;
	}

	public SharedRuleElementDiff getDiffElement() {
		return diffElement;
	}

	public String getCreator() {
		return creator;
	}

}
