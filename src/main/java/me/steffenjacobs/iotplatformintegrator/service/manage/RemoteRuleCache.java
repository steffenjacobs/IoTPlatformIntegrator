package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RuleAnalyzer;

/** @author Steffen Jacobs */
public class RemoteRuleCache implements RuleAnalyzer {

	private final Map<String, SharedRule> cache = new HashMap<>();

	public RemoteRuleCache() {
		EventBus.getInstance().addEventHandler(EventType.CLEAR_ALL_REMOTE_RULES, e -> cache.clear());
		EventBus.getInstance().addEventHandler(EventType.REMOTE_RULE_ADDED,
				e -> cache.put(((RemoteRuleAddedEvent) e).getSelectedRule().getName(), ((RemoteRuleAddedEvent) e).getSelectedRule()));
	}

	public SharedRule getRuleByName(String name) {
		return cache.get(name);
	}

	public Iterable<SharedRule> getRules() {
		return cache.values();
	}

	public Iterable<SharedRule> getRulesWithItemNameContaining(String searchText) {
		if(searchText == null || searchText.isEmpty()) {
			return new ArrayList<SharedRule>();
		}
		final Collection<SharedRule> rulesWithItemNames = new ArrayList<>();
		for (SharedRule rule : cache.values()) {
			if (aggregateItemsFromRuleWithoutDuplicates(rule).stream().flatMap(s -> Arrays.asList(s.getName(), s.getLabel()).stream())
					.filter(s -> s.toLowerCase().contains(searchText.toLowerCase())).count() > 0) {
				rulesWithItemNames.add(rule);
			}
		}
		return rulesWithItemNames;
	}
}
