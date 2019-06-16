package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabItemService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PseudocodeGenerator;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;

/** @author Steffen Jacobs */
public class UiEntrypointController {

	private static final Logger LOG = LoggerFactory.getLogger(UiEntrypointController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();
	private static final OpenHabItemService itemService = new OpenHabItemService();
	private static final PlatformTransformationAdapter<ItemDTO, ExperimentalRule> transformer = new OpenHabTransformationAdapter();
	private static final PseudocodeGenerator pseudocodeGenerator = new PseudocodeGenerator();

	private final SettingService settingService;
	private UiEntrypoint ui;

	private final List<SharedRule> loadedRules = new ArrayList<>();
	private final Map<String, SharedItem> loadedItems = new HashMap<>();

	public UiEntrypointController(SettingService settingService) {
		this.settingService = settingService;
	}

	public void setUi(UiEntrypoint ui) {
		this.ui = ui;
	}

	public void loadOpenHABRules() throws IOException {
		loadedRules.clear();
		final List<ExperimentalRule> retrievedRules = ruleService.requestAllRules(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} rules.", retrievedRules.size());
		for (ExperimentalRule rule : retrievedRules) {
			loadedRules.add(transformer.getRuleTransformer().transformRule(rule));
		}
		ui.refreshRulesTable(loadedRules);
	}

	public void loadOpenHABItems() throws IOException {
		loadedItems.clear();
		final List<ItemDTO> retrievedItems = itemService.requestItems(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} items.", retrievedItems.size());
		for (ItemDTO item : retrievedItems) {
			SharedItem transformedItem = transformer.getItemTransformer().transformItem(item);
			loadedItems.put(transformedItem.getName(), transformedItem);
		}
		ui.refreshItems(loadedItems.values());
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
