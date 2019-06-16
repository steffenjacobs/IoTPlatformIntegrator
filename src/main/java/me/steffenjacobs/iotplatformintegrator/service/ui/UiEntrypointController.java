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
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;

/** @author Steffen Jacobs */
public class UiEntrypointController {

	private static final Logger LOG = LoggerFactory.getLogger(UiEntrypointController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();
	private static final OpenHabItemService itemService = new OpenHabItemService();

	private final SettingService settingService;
	private UiEntrypoint ui;

	private final List<SharedRule> loadedRules = new ArrayList<>();
	private final ItemDirectory itemDirectory;
	private final PlatformTransformationAdapter<ItemDTO, ExperimentalRule> transformer;
	private final CodeEditorController codeEditorController;

	private SharedRule lastRule = null;

	public UiEntrypointController(SettingService settingService, CodeEditor codeEditor) {
		this.settingService = settingService;
		itemDirectory = new ItemDirectory();
		transformer = new OpenHabTransformationAdapter(itemDirectory);
		codeEditorController = new CodeEditorController(codeEditor, settingService);
		codeEditor.setController(codeEditorController);
	}

	public CodeEditorController getCodeEditorController() {
		return codeEditorController;
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
		lastRule = null;
	}

	public void loadOpenHABItems() throws IOException {
		itemDirectory.clearItems();
		final List<ItemDTO> retrievedItems = itemService.requestItems(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} items.", retrievedItems.size());
		for (ItemDTO item : retrievedItems) {
			SharedItem transformedItem = transformer.getItemTransformer().transformItem(item);
			itemDirectory.addItem(transformedItem);
		}
		ui.refreshItems(itemDirectory.getAllItems());

		// avoid generating rule code with stale items
		lastRule = null;
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

	public void renderPseudocode(SharedRule rule) {
		if (lastRule != rule) {
			codeEditorController.renderPseudocode(rule);
			lastRule = rule;
		}

	}

}
