package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.ArrayList;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;

/** @author Steffen Jacobs */
public class RuleDiffManager {

	private SharedRule rule = null;
	private String creator;
	private final List<SharedRuleElementDiff> diffs = new ArrayList<>();

	public RuleDiffManager(MongoDbRuleDiffStorageService ruleDiffStorageService) {

		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> {
			RuleDiffChangeEvent event = (RuleDiffChangeEvent) e;
			if (rule != event.getSelectedRule()) {
				rule = event.getSelectedRule();
				diffs.clear();
				creator = event.getCreator();
			}
			diffs.add(event.getDiffElement());
		});

		EventBus.getInstance().addEventHandler(EventType.StoreRuleToDatabase, e -> {
			diffs.get(diffs.size() - 1).setTargetRuleName(((StoreRuleToDatabaseEvent) e).getNewRuleName());
			diffs.forEach(d -> ruleDiffStorageService.store(d, rule, creator));
			diffs.clear();
			rule = null;
		});
	}

}
