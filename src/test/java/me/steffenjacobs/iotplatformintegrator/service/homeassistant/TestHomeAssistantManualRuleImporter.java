package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in.HomeAssistantItemTransformationService;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in.HomeAssistantManualRuleImporter;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class TestHomeAssistantManualRuleImporter {

	@Test
	public void testImportRules() throws ClientProtocolException, IOException {
		SettingService settingService = new SettingService("./settings.config");
		HomeAssistantManualRuleImporter importer = new HomeAssistantManualRuleImporter(settingService);
		ItemDirectory itemDirectory = new ItemDirectory();
		HomeAssistantApiService homeAssistantApiService = new HomeAssistantApiService();
		HomeAssistantItemTransformationService haItemTransformationService = new HomeAssistantItemTransformationService();

		Pair<List<SharedItem>, List<SharedRule>> itemsAndRules = haItemTransformationService.transformItemsAndRules(
				homeAssistantApiService.getAllState(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
		itemDirectory.addItems(itemsAndRules.getLeft());
		List<SharedRule> rules = importer.importRules(new File(settingService.getSetting(SettingKey.HOMEASSISTANT_FILE_URI)), itemDirectory);
		System.out.println(rules);
	}
}
