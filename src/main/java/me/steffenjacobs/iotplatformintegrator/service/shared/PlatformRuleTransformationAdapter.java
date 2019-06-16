package me.steffenjacobs.iotplatformintegrator.service.shared;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public interface PlatformRuleTransformationAdapter<Rule> {

	SharedRule transformRule(Rule rule);
}