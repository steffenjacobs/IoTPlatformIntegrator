package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedSourceRuleChangeEvent extends WithSharedRuleEvent {

	public SelectedSourceRuleChangeEvent(SharedRule selectedRule) {
		super(EventType.SelectedSourceRuleChanged, selectedRule);
	}
}
