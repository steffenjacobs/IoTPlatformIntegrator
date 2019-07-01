package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;

/** @author Steffen Jacobs */
public class MongoDbRuleDiffStorageService {

	private final MongoDbStorageService storageService = new MongoDbStorageService();
	private final SharedRuleElementDiffJsonTransformer diffTransformer = new SharedRuleElementDiffJsonTransformer();
	private final SharedRuleElementDiffDocumentTransformer documentTransformer = new SharedRuleElementDiffDocumentTransformer();

	public MongoDbRuleDiffStorageService() {
		storageService.connect();
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> {
			RuleDiffChangeEvent event = (RuleDiffChangeEvent) e;
			storageService.insert(documentTransformer.toDocument(diffTransformer.toJSON(event.getDiffElement())));
		});
	}

}
