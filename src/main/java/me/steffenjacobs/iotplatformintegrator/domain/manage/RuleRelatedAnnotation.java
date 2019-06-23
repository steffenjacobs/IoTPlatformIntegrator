package me.steffenjacobs.iotplatformintegrator.domain.manage;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

/** @author Steffen Jacobs */
public class RuleRelatedAnnotation {
	private final int index;
	private final SharedTypeSpecificKey ruleElementSpecificKey;

	public RuleRelatedAnnotation(int index, SharedTypeSpecificKey ruleElementSpecificKey) {
		super();
		this.index = index;
		this.ruleElementSpecificKey = ruleElementSpecificKey;
	}

	public int getIndex() {
		return index;
	}

	public SharedTypeSpecificKey getRuleElementSpecificKey() {
		return ruleElementSpecificKey;
	}
}
