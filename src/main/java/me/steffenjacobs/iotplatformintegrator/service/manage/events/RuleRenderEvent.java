package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleRenderEvent extends WithSharedRuleEvent {

	public RuleRenderEvent(SharedRule sharedRule) {
		super(EventType.RULE_RENDER, sharedRule);
	}

}
