package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class TestHomeAssistantManualRuleImporter {

	@Test
	public void testImportRules() throws ClientProtocolException, IOException {
		HomeAssistantManualRuleImporter importer = new HomeAssistantManualRuleImporter();
		ItemDirectory itemDirectory = new ItemDirectory();
		HomeAssistantApiService homeAssistantApiService = new HomeAssistantApiService();
		HomeAssistantItemTransformationService haItemTransformationService = new HomeAssistantItemTransformationService();
		SettingService settingService = new SettingService("./settings.config");

		Pair<List<SharedItem>, List<SharedRule>> itemsAndRules = haItemTransformationService.transformItemsAndRules(
				homeAssistantApiService.getAllState(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
		itemDirectory.addItems(itemsAndRules.getLeft());
		List<SharedRule> rules = importer.importRules(new File("L:\\Dropbox\\Masterarbeit\\test.yaml"), itemDirectory);
		System.out.println(rules);
	}
}
