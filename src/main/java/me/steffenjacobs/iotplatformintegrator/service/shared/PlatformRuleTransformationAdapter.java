package me.steffenjacobs.iotplatformintegrator.service.shared;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in.OpenHabCommandParser;

/** @author Steffen Jacobs */
public interface PlatformRuleTransformationAdapter<Rule> {

	SharedRule transformRule(Rule rule, ItemDirectory itemDirectory, OpenHabCommandParser commandParser);
}
