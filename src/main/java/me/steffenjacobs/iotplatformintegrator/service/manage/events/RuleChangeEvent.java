package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleChangeEvent extends WithSharedRuleEvent {

	private final SharedRuleElement newElement;
	private final SharedRuleElement oldElement;

	public RuleChangeEvent(SharedRule sharedRule, SharedRuleElement newElement, SharedRuleElement oldElement) {
		super(EventType.RuleChange, sharedRule);
		this.newElement = newElement;
		this.oldElement = oldElement;
	}

	public SharedRuleElement getNewElement() {
		return newElement;
	}

	public SharedRuleElement getOldElement() {
		return oldElement;
	}

}
