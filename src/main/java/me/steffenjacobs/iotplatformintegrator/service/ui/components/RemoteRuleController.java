package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.util.function.Consumer;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;

/** @author Steffen Jacobs */
public class RemoteRuleController {

	private final MongoDbRuleDiffStorageService diffStorage;

	public RemoteRuleController(MongoDbRuleDiffStorageService diffStorage) {
		this.diffStorage = diffStorage;
	}

	public void getDiffs(SharedRule rule, Consumer<SharedRuleElementDiff> consumer) {
		diffStorage.findForRule(rule, new SimplifiedSubscriber<SharedRuleElementDiff>() {
			@Override
			public void onNext(SharedRuleElementDiff diff) {
				consumer.accept(diff);
			}
		});
	}

}
