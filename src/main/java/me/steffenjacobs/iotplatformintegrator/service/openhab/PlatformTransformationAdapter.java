package me.steffenjacobs.iotplatformintegrator.service.openhab;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public interface PlatformTransformationAdapter<Rule, Item> {

	SharedRule transformRule(Rule rule);

	SharedItem transformItem(Item item);

}