package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbRuleDiffStorageService {

	private final SharedRuleElementDiffJsonTransformer diffTransformer = new SharedRuleElementDiffJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbRuleDiffStorageService(MongoDbStorageService storageService) {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> {
			RuleDiffChangeEvent event = (RuleDiffChangeEvent) e;
			store(event.getDiffElement());
		});
	}

	public void store(SharedRuleElementDiff diff) {
		storageService.insert(documentTransformer.toDocument(diffTransformer.toJSON(diff)));
	}

}
