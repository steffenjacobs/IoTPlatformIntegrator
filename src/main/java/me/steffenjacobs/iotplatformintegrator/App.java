package me.steffenjacobs.iotplatformintegrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationService;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationServiceImpl;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedRuleStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbUserStorageService;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleChangeEventStore;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static MongoDbRuleDiffStorageService mongoDbRuleDiffStorageService;
	private static MongoDbSharedRuleStorageService mongoDbSharedRuleStorageService;
	private static MongoDbUserStorageService mongoDbUserStorageService;
	private static AuthenticationService authenticationService;

	public static void main(String[] args) {
		LOG.info("Started.");
		final MongoDbStorageService storageService = new MongoDbStorageService();
		mongoDbRuleDiffStorageService = new MongoDbRuleDiffStorageService(storageService);
		mongoDbSharedRuleStorageService = new MongoDbSharedRuleStorageService(storageService);
		mongoDbUserStorageService = new MongoDbUserStorageService(storageService);

		SettingService settingService = new SettingService("./settings.config");
		authenticationService = new AuthenticationServiceImpl(settingService);

		new UiEntrypoint(settingService, authenticationService).createAndShowGUIAsync();
		new RuleChangeEventStore(authenticationService);

		LOG.info("Setup complete.");
	}

	public static MongoDbRuleDiffStorageService getMongoDbRuleDiffStorageService() {
		return mongoDbRuleDiffStorageService;
	}

	public static MongoDbSharedRuleStorageService getMongoDbSharedRuleStorageService() {
		return mongoDbSharedRuleStorageService;
	}

	public static MongoDbUserStorageService getMongoDbUserStorageService() {
		return mongoDbUserStorageService;
	}

}
