package me.steffenjacobs.iotplatformintegrator.service.shared;

/** @author Steffen Jacobs */
public interface PlatformReverseTransformationAdapter<Item, Rule> {
	PlatformItemReverseTransformationAdapter<Item> getItemTransformer();

	PlatformRuleReverseTransformationAdapter<Rule> getRuleTransformer();
}
