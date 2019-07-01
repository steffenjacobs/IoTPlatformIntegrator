package me.steffenjacobs.iotplatformintegrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedRuleStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbStorageService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleChangeEventStore;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		LOG.info("Started.");
		new UiEntrypoint().createAndShowGUIAsync();
		new RuleChangeEventStore();

		final MongoDbStorageService storageService = new MongoDbStorageService();
		new MongoDbRuleDiffStorageService(storageService);
		new MongoDbSharedRuleStorageService(storageService);
	}
}
