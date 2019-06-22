package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleElementAddedEvent extends WithRuleElementEvent {

	public RuleElementAddedEvent(ElementType elementType, UUID sourceId) {
		super(EventType.RuleElementAdded, elementType, sourceId);
	}

}
