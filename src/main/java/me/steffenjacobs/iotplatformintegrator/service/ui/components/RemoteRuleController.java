package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.util.function.Consumer;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
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

}
