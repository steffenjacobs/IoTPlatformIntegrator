package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleElementCreatedEvent extends WithSharedRuleEvent {

	private final ElementType elementType;
	private final SharedElementType sharedElementType;

	public RuleElementCreatedEvent(SharedRule sharedRule, ElementType elementType, SharedElementType sharedElementType) {
		super(EventType.RULE_ELEMENT_CREATED, sharedRule);
		// TODO Auto-generated constructor stub
		this.elementType = elementType;
		this.sharedElementType = sharedElementType;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public SharedElementType getSharedElementType() {
		return sharedElementType;
	}

}
