package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public class MongoDbSharedRuleStorageService {

	private final SharedRuleJsonTransformer jsonTransformer = new SharedRuleJsonTransformer();
	private final SharedRuleElementDiffDocumentTransformer documentTransformer = new SharedRuleElementDiffDocumentTransformer();
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
