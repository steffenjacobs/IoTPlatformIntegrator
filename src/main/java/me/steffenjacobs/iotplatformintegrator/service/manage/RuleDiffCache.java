package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class RuleDiffCache {

	private final Map<String, RuleDiffParts> cache = new HashMap<>();

	public RuleDiffCache() {
		EventBus.getInstance().addEventHandler(EventType.RuleDiffAdded,
				e -> cache.put(((RuleDiffAddedEvent) e).getRuleDiffParts().getRuleDiff().getUid().toString(), ((RuleDiffAddedEvent) e).getRuleDiffParts()));
		EventBus.getInstance().addEventHandler(EventType.RefreshRuleDiffs, e -> cache.clear());
	}

	public RuleDiffParts getRuleDiffParts(String id) {
		return cache.get(id);
	}

}
