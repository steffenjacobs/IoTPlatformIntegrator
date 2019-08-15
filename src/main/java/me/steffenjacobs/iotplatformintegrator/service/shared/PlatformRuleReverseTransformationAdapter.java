package me.steffenjacobs.iotplatformintegrator.service.shared;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out.OpenHabCommandReverseTransformer;

/** @author Steffen Jacobs */
public interface PlatformRuleReverseTransformationAdapter<Rule> {

	Rule transformRule(SharedRule rule, ItemDirectory itemDirectory, OpenHabCommandReverseTransformer reverseCommandParser);
}
