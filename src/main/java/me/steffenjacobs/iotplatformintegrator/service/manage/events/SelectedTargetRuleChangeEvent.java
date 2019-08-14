package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedTargetRuleChangeEvent extends WithSharedRuleEvent {

	public SelectedTargetRuleChangeEvent(SharedRule selectedRule) {
		super(EventType.SELECTED_TARGET_RULE_CHANGE, selectedRule);
	}
}
