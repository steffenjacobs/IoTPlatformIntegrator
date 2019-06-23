package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleElementChangeEvent extends WithRuleElementAndRuleEvent {

	public RuleElementChangeEvent(ElementType elementType, UUID elementUUID, SharedRule sharedRule) {
		super(EventType.RuleElementChangeEvent, elementType, elementUUID, sharedRule);
	}

}
