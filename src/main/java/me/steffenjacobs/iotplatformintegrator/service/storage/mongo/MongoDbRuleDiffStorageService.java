package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.UserScoreJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbRuleDiffStorageService {

	private final SharedRuleElementDiffJsonTransformer diffTransformer = new SharedRuleElementDiffJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final UserScoreJsonTransformer userScoreTransformer = new UserScoreJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbRuleDiffStorageService(MongoDbStorageService storageService) throws IOException {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
	}

	public void store(SharedRuleElementDiff diff, SharedRule associatedRule, String creator) {
		storageService.insertDiff(documentTransformer.toDocument(diffTransformer.toJSON(diff, associatedRule.getName(), creator)));
	}

	public void findForRule(SharedRule rule, SimplifiedSubscriber<Pair<SharedRuleElementDiff, String>> subscriber) {
		storageService.findDiffsForRule(rule, subscriber, d -> diffTransformer.fromJSON(documentTransformer.toJSON(d)));
	}

	public void getStats(SimplifiedSubscriber<UserScore> subscriber) {
		storageService.getStats(subscriber, d -> userScoreTransformer.fromJSON(documentTransformer.toJSON(d)));
	}

	public void getAllDiffs(SimplifiedSubscriber<Pair<SharedRuleElementDiff, String>> subscriber) {
		storageService.getAllDiffs(subscriber, d -> diffTransformer.fromJSON(documentTransformer.toJSON(d)));

	}

}
