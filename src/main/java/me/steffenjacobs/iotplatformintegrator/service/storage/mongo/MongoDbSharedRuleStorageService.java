package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbSharedRuleStorageService {

	private final SharedRuleJsonTransformer jsonTransformer = new SharedRuleJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbSharedRuleStorageService(MongoDbStorageService storageService) {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
		// TODO: add correct event handler
	}

	public void store(SharedRule rule) {
		storageService.insert(documentTransformer.toDocument(jsonTransformer.toJson(rule)));
	}
}
