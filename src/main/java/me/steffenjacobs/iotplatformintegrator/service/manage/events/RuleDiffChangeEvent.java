package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleDiffChangeEvent extends WithSharedRuleEvent {

	private final SharedRuleElementDiff diffElement;
	private final String creator;

	public RuleDiffChangeEvent(SharedRule sharedRule, SharedRuleElementDiff diffElement, String creator) {
		super(EventType.RULE_DIFF_CHANGE, sharedRule);
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
