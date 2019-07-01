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

	private static MongoDbRuleDiffStorageService mongoDbRuleDiffStorageService;
	private static MongoDbSharedRuleStorageService mongoDbSharedRuleStorageService;

	public static void main(String[] args) {
		LOG.info("Started.");
		final MongoDbStorageService storageService = new MongoDbStorageService();
		mongoDbRuleDiffStorageService = new MongoDbRuleDiffStorageService(storageService);
		mongoDbSharedRuleStorageService = new MongoDbSharedRuleStorageService(storageService);

		new UiEntrypoint().createAndShowGUIAsync();
		new RuleChangeEventStore();

		LOG.info("Setup complete.");
	}

	public static MongoDbRuleDiffStorageService getMongoDbRuleDiffStorageService() {
		return mongoDbRuleDiffStorageService;
	}

	public static MongoDbSharedRuleStorageService getMongoDbSharedRuleStorageService() {
		return mongoDbSharedRuleStorageService;
	}

}
