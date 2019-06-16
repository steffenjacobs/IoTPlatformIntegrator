package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabItemService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabRuleTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.openhab.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PseudocodeGenerator;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;

/** @author Steffen Jacobs */
public class UiEntrypointController {

	private static final Logger LOG = LoggerFactory.getLogger(UiEntrypointController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();
	private static final OpenHabItemService itemService = new OpenHabItemService();
	private static final PlatformTransformationAdapter<ExperimentalRule, ItemDTO> transformer = new OpenHabRuleTransformationAdapter();
	private static final PseudocodeGenerator pseudocodeGenerator = new PseudocodeGenerator();

	private final SettingService settingService;
	private UiEntrypoint ui;

	private final List<SharedRule> loadedRules = new ArrayList<>();
	private final List<SharedItem> loadedItems = new ArrayList<>();

	public UiEntrypointController(SettingService settingService) {
		this.settingService = settingService;
	}

	public void setUi(UiEntrypoint ui) {
		this.ui = ui;
	}

	public void loadOpenHABRules() throws IOException {
		loadedRules.clear();
		LOG.info("Retrieved {} rules.", loadedRules.size());
		for (ExperimentalRule rule : ruleService.requestAllRules(settingService.getSetting(SettingKey.OPENHAB_URI))) {
			loadedRules.add(transformer.transformRule(rule));
		}
		ui.refreshRulesTable(loadedRules);
	}

	public void loadOpenHABItems() throws IOException {
		loadedItems.clear();
		LOG.info("Retrieved {} items.", loadedItems.size());
		for (ItemDTO item : itemService.requestItems(settingService.getSetting(SettingKey.OPENHAB_URI))) {
			loadedItems.add(transformer.transformItem(item));
		}
		ui.refreshItems(loadedItems);
	}

	public SharedRule getRuleByIndex(int index) {
		if (index < 0 || index >= loadedRules.size()) {
			return null;
		}
		return loadedRules.get(index);
	}

	public String getUrlWithPort() {
		return settingService.getSetting(SettingKey.OPENHAB_URI);
	}

	public String getPseudocode(SharedRule rule) {
		return pseudocodeGenerator.generateCodeForRule(rule);
	}

}
