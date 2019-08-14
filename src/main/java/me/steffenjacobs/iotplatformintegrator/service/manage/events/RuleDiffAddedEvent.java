package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class RuleDiffAddedEvent extends WithRuleDiffEvent {

	public RuleDiffAddedEvent(RuleDiffParts ruleDiffParts) {
		super(EventType.RULE_DIFF_ADDED, ruleDiffParts);
	}

}