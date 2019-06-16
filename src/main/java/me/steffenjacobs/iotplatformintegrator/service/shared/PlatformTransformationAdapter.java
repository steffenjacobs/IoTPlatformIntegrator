package me.steffenjacobs.iotplatformintegrator.service.shared;

/** @author Steffen Jacobs */
public interface PlatformTransformationAdapter<Item, Rule> {
	PlatformItemTransformationAdapter<Item> getItemTransformer();

	PlatformRuleTransformationAdapter<Rule> getRuleTransformer();
}
