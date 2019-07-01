package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;

/** @author Steffen Jacobs */
public class MongoDbRuleDiffStorageService {

	private final SharedRuleElementDiffJsonTransformer diffTransformer = new SharedRuleElementDiffJsonTransformer();
	private final SharedRuleElementDiffDocumentTransformer documentTransformer = new SharedRuleElementDiffDocumentTransformer();
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
