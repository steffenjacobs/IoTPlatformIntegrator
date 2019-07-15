package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.UserScoreJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbRuleDiffStorageService {

	private final SharedRuleElementDiffJsonTransformer diffTransformer = new SharedRuleElementDiffJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final UserScoreJsonTransformer userScoreTransformer = new UserScoreJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbRuleDiffStorageService(MongoDbStorageService storageService) {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> {
			RuleDiffChangeEvent event = (RuleDiffChangeEvent) e;
			store(event.getDiffElement(), event.getSelectedRule());
		});
	}

	public void store(SharedRuleElementDiff diff, SharedRule associatedRule) {
		storageService.insertDiff(documentTransformer.toDocument(diffTransformer.toJSON(diff, associatedRule.getName())));
	}

	public void findForRule(SharedRule rule, SimplifiedSubscriber<SharedRuleElementDiff> subscriber) {
		storageService.findDiffsForRule(rule, subscriber, d -> diffTransformer.fromJSON(documentTransformer.toJSON(d)));
	}

	public void getStats(SimplifiedSubscriber<UserScore> subscriber) {
		storageService.getStats(subscriber, d -> userScoreTransformer.fromJSON(documentTransformer.toJSON(d)));
	}

}
