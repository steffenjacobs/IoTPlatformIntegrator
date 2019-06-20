package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedRuleChangedEvent extends WithSharedRuleEvent {

	public SelectedRuleChangedEvent(SharedRule selectedRule) {
		super(EventType.SelectedRuleChanged, selectedRule);
	}
}
