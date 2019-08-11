package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class RuleDiffAddedEvent extends Event {

	private RuleDiffParts ruleDiffParts;

	public RuleDiffAddedEvent(RuleDiffParts ruleDiffParts) {
		super(EventType.RuleDiffAdded);
		this.ruleDiffParts = ruleDiffParts;
	}

	public RuleDiffParts getRuleDiffParts() {
		return ruleDiffParts;
	}
}