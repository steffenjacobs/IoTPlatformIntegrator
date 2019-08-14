package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class SelectedRuleDiffChangeEvent extends WithRuleDiffEvent {

	public SelectedRuleDiffChangeEvent(RuleDiffParts ruleDiffParts) {
		super(EventType.SELECTED_RULE_DIFF_CHANGE, ruleDiffParts);
	}
}
