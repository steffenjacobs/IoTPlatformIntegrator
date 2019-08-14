package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RemoteRuleChangeEvent extends WithSharedRuleEvent {

	public RemoteRuleChangeEvent(SharedRule sharedRule) {
		super(EventType.REMOTE_RULE_CHANGE, sharedRule);
	}

}
