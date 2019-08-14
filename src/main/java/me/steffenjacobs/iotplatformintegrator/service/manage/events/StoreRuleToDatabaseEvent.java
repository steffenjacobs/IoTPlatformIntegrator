package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class StoreRuleToDatabaseEvent extends WithSharedRuleEvent {

	private final String newRuleName;
	private boolean uploadRule;

	public StoreRuleToDatabaseEvent(SharedRule sharedRule, String newRuleName, boolean uploadRule) {
		super(EventType.StoreRuleToDatabase, sharedRule);
		this.newRuleName = newRuleName;
		this.uploadRule = uploadRule;
	}

	public String getNewRuleName() {
		return newRuleName;
	}

	public boolean isUploadRule() {
		return uploadRule;
	}

}
