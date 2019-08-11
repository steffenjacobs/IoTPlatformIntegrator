package me.steffenjacobs.iotplatformintegrator;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationService;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationServiceImpl;
import me.steffenjacobs.iotplatformintegrator.service.manage.RuleDiffManager;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedItemStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedRuleStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbUserStorageService;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RemoteRuleController;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleChangeEventStore;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer.RuleGraphManager;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static MongoDbRuleDiffStorageService mongoDbRuleDiffStorageService;
	private static MongoDbSharedRuleStorageService mongoDbSharedRuleStorageService;
	private static MongoDbUserStorageService mongoDbUserStorageService;
	private static MongoDbSharedItemStorageService mongoDbSharedItemStorageService;
	private static AuthenticationService authenticationService;
	private static RemoteRuleController remoteRuleController;
	
	private static MongoDbStorageService storageService;
	
	private static RuleGraphManager ruleGraphManager;

	public static void main(String[] args) {
		LOG.info("Started.");
		final SettingService settingService = new SettingService("./settings.config");
		storageService = new MongoDbStorageService(settingService);
		try {
			mongoDbRuleDiffStorageService = new MongoDbRuleDiffStorageService(storageService);
			mongoDbSharedItemStorageService = new MongoDbSharedItemStorageService(storageService);
			mongoDbSharedRuleStorageService = new MongoDbSharedRuleStorageService(storageService);
			mongoDbUserStorageService = new MongoDbUserStorageService(storageService);

			authenticationService = new AuthenticationServiceImpl(settingService);
			ruleGraphManager = new RuleGraphManager();

			new UiEntrypoint(settingService, authenticationService).createAndShowGUIAsync();
			new RuleChangeEventStore(authenticationService);

			remoteRuleController = new RemoteRuleController(mongoDbRuleDiffStorageService, mongoDbSharedRuleStorageService, mongoDbSharedItemStorageService);

			new RuleDiffManager(mongoDbRuleDiffStorageService);
			

			LOG.info("Setup complete.");
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("Could not connect to database.");
			JOptionPane.showMessageDialog(null, String.format("Could not connect ot database %s", settingService.getSetting(SettingKey.DATABASE_URI)), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
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

	public static RemoteRuleController getRemoteRuleController() {
		return remoteRuleController;
	}

	public static ServerConnection getDatabaseConnectionObject() {
		return storageService.getDatabaseConnection();
	}

	public static MongoDbSharedItemStorageService getMongoDbSharedItemStorageService() {
		return mongoDbSharedItemStorageService;
	}
	
	public static RuleGraphManager getRuleGraphManager() {
		return ruleGraphManager;
	}

}
