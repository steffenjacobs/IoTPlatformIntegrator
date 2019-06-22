package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class WithRuleElementEvent extends Event {

	private final ElementType elementType;
	private final UUID sourceId;

	protected WithRuleElementEvent(EventType eventType, ElementType elementType, UUID sourceId) {
		super(eventType);
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
