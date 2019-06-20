package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedRuleChangeEvent extends WithSharedRuleEvent {

	public SelectedRuleChangeEvent(SharedRule selectedRule) {
		super(EventType.SelectedRuleChanged, selectedRule);
	}
}
