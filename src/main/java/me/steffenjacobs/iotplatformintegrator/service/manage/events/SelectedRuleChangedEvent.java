package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedRuleChangedEvent extends Event {

	private final SharedRule selectedRule;

	public SelectedRuleChangedEvent(SharedRule selectedRule) {
		super(EventType.SelectedRuleChanged);
		this.selectedRule = selectedRule;
	}

	public SharedRule getSelectedRule() {
		return selectedRule;
	}

}
