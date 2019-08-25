package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;

/** @author Steffen Jacobs */
public class RemoteRuleCache {

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

}
