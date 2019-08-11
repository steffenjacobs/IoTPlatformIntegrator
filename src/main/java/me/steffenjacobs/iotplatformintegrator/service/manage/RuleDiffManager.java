package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;

/** @author Steffen Jacobs */
public class RuleDiffManager {

	private SharedRule rule = null;
	private String creator;
	private final List<SharedRuleElementDiff> diffs = new ArrayList<>();
	private MongoDbRuleDiffStorageService ruleDiffStorageService;

	public RuleDiffManager(MongoDbRuleDiffStorageService ruleDiffStorageService) {

		this.ruleDiffStorageService = ruleDiffStorageService;
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> {
			RuleDiffChangeEvent event = (RuleDiffChangeEvent) e;
			if (rule != event.getSelectedRule()) {
				rule = event.getSelectedRule();
				diffs.clear();
				creator = event.getCreator();
			}
			event.getDiffElement().setSourceRule(rule);
			if(!diffs.isEmpty()) {
				event.getDiffElement().setPrevDiff(diffs.get(diffs.size()-1));
			}
			diffs.add(event.getDiffElement());
		});

		EventBus.getInstance().addEventHandler(EventType.StoreRuleToDatabase, e -> {
			if(!diffs.isEmpty()) {
				diffs.get(diffs.size() - 1).setTargetRuleName(((StoreRuleToDatabaseEvent) e).getNewRuleName());
			}
			diffs.forEach(d -> ruleDiffStorageService.store(d, rule, creator));
			diffs.clear();
			rule = null;
		});
		
		refreshRemoteDiffs();
	}
	
	private void refreshRemoteDiffs() {
		 ruleDiffStorageService.getAllDiffs(new SimplifiedSubscriber<Pair<SharedRuleElementDiff, String>>() {
			 @Override
			public void onNext(Pair<SharedRuleElementDiff, String> diffWithCreator) {
				 EventBus.getInstance().fireEvent(new RuleDiffAddedEvent(diffWithCreator.getLeft(), diffWithCreator.getRight()));
			}
		});
	}

}
