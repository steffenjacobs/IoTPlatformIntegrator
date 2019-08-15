package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleElementCopiedEvent extends WithRuleElementEvent {

	public RuleElementCopiedEvent(ElementType elementType, UUID sourceId) {
		super(EventType.RULE_ELEMENT_COPIED, elementType, sourceId);
	}

}
