package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class StoreRuleToDatabaseEvent extends WithSharedRuleEvent {

	private final String newRuleName;

	public StoreRuleToDatabaseEvent(SharedRule sharedRule, String newRuleName) {
		super(EventType.StoreRuleToDatabase, sharedRule);
		this.newRuleName = newRuleName;
	}

	public String getNewRuleName() {
		return newRuleName;
	}

}
