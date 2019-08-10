package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;

import org.reactivestreams.Subscriber;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbSharedRuleStorageService {

	private final SharedRuleJsonTransformer jsonTransformer = new SharedRuleJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbSharedRuleStorageService(MongoDbStorageService storageService) throws IOException {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
		// TODO: add correct event handler
	}

	public void store(SharedRule rule) {
		storageService.insertDiff(documentTransformer.toDocument(jsonTransformer.toJson(rule)));
	}

	public void getRules(Subscriber<SharedRule> subscriber) {
		storageService.getAllRules(subscriber, d -> jsonTransformer.fromJson(documentTransformer.toJSON(d)));
	}

	public void insertRule(SharedRule rule, Runnable callWhenDone) {
		storageService.insertRule(documentTransformer.toDocument(jsonTransformer.toJson(rule)), callWhenDone);
	}
}
