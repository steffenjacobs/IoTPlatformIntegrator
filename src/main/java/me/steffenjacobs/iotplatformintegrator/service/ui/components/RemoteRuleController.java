package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.util.function.Consumer;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ClearAllRemoteRulesEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedRuleStorageService;

/** @author Steffen Jacobs */
public class RemoteRuleController {

	private final MongoDbRuleDiffStorageService diffStorage;
	private final MongoDbSharedRuleStorageService ruleStorage;

	public RemoteRuleController(MongoDbRuleDiffStorageService diffStorage, MongoDbSharedRuleStorageService ruleStorage) {
		this.diffStorage = diffStorage;
		this.ruleStorage = ruleStorage;

		EventBus.getInstance().addEventHandler(EventType.StoreRuleToDatabase, e -> {
			StoreRuleToDatabaseEvent event = (StoreRuleToDatabaseEvent) e;
			SharedRule rule = new SharedRule(event.getNewRuleName(), event.getSelectedRule());
			uploadRule(rule);
		});
		
		refreshRules();
	}

	public void getDiffs(SharedRule rule, Consumer<SharedRuleElementDiff> consumer) {
		diffStorage.findForRule(rule, new SimplifiedSubscriber<SharedRuleElementDiff>() {
			@Override
			public void onNext(SharedRuleElementDiff diff) {
				consumer.accept(diff);
			}
		});
	}

	public void getRules(Consumer<SharedRule> consumer) {
		ruleStorage.getRules(new SimplifiedSubscriber<SharedRule>() {
			@Override
			public void onNext(SharedRule t) {
				consumer.accept(t);
			}
		});
	}

	public void uploadRule(SharedRule selectedRule) {
		ruleStorage.insertRule(selectedRule, this::refreshRules);
	}

	private void refreshRules() {
		EventBus.getInstance().fireEvent(new ClearAllRemoteRulesEvent());
		getRules(r -> EventBus.getInstance().fireEvent(new RemoteRuleAddedEvent(r)));
	}
}
