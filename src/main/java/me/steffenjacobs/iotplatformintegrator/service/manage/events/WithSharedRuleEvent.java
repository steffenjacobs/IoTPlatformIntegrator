package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class WithSharedRuleEvent extends Event {

	private final SharedRule selectedRule;

	public WithSharedRuleEvent(EventType eventType, SharedRule sharedRule) {
		super(eventType);
		selectedRule = sharedRule;

	}

	public SharedRule getSelectedRule() {
		return selectedRule;
	}

}
