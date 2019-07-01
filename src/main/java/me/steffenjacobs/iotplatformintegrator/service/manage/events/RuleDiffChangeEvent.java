package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleDiffChangeEvent extends WithSharedRuleEvent {

	private final SharedRuleElementDiff diffElement;

	public RuleDiffChangeEvent(SharedRule sharedRule, SharedRuleElementDiff diffElement) {
		super(EventType.RuleDiffChangeEvent, sharedRule);
		this.diffElement = diffElement;
	}

	public SharedRuleElementDiff getDiffElement() {
		return diffElement;
	}
}
