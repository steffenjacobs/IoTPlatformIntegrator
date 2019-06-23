package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class WithRuleElementAndRuleEvent extends WithSharedRuleEvent {

	private final ElementType elementType;
	private final UUID sourceId;

	protected WithRuleElementAndRuleEvent(EventType eventType, ElementType elementType, UUID sourceId, SharedRule rule) {
		super(eventType, rule);
		this.elementType = elementType;
		this.sourceId = sourceId;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public UUID getSourceId() {
		return sourceId;
	}

}
