package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public class RuleValidator {
	public boolean containsEmptyRules(List<SharedRule> rules) {
		for (SharedRule rule : rules) {
			if (rule.getTriggers().isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
