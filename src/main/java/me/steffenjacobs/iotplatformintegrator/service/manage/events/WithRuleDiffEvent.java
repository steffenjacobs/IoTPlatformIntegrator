package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class WithRuleDiffEvent extends Event {
	private RuleDiffParts ruleDiffParts;

	WithRuleDiffEvent(EventType eventType, RuleDiffParts ruleDiffParts) {
		super(eventType);
		this.ruleDiffParts = ruleDiffParts;
	}

	public RuleDiffParts getRuleDiffParts() {
		return ruleDiffParts;
	}
}
