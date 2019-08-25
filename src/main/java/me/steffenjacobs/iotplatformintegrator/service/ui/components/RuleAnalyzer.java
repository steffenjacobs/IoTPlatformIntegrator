package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public interface RuleAnalyzer {
	default Collection<SharedItem> aggregateItemsFromRuleWithoutDuplicates(SharedRule rule) {
		return aggregateItemsFromRule(rule, new HashSet<>());
	}

	default Collection<SharedItem> aggregateItemsFromRuleWithDuplicates(SharedRule rule) {
		return aggregateItemsFromRule(rule, new ArrayList<>());
	}

	default Collection<SharedItem> aggregateItemsFromRule(SharedRule rule, Collection<SharedItem> items) {
		rule.getActions().forEach(a -> a.getActionTypeContainer().getActionTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		rule.getConditions().forEach(c -> c.getConditionTypeContainer().getConditionTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		rule.getTriggers().forEach(t -> t.getTriggerTypeContainer().getTriggerTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		return items;
	}
}
