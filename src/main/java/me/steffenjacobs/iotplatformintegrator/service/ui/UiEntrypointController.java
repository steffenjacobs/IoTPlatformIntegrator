package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;

/** @author Steffen Jacobs */
public class UiEntrypointController {

	private static final Logger LOG = LoggerFactory.getLogger(UiEntrypointController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();

	private final SettingService settingService;
	private UiEntrypoint ui;

	private final List<ExperimentalRule> loadedRules = new ArrayList<>();

	public UiEntrypointController(SettingService settingService) {
		this.settingService = settingService;
	}

	public void setUi(UiEntrypoint ui) {
		this.ui = ui;
	}

	public void loadOpenHABRules() {
		loadedRules.clear();
		loadedRules.addAll(ruleService.requestAllRules(settingService.getSetting(SettingKey.OPENHAB_URI)));
		for (ExperimentalRule rule : loadedRules) {
			LOG.info("Retrieved rule '{}'", rule.getName());
		}
		LOG.info("Retrieved {} rules.", loadedRules.size());
		ui.refreshTable(loadedRules);
	}

	public ExperimentalRule getRuleByIndex(int index) {
		if (index < 0 || index >= loadedRules.size()) {
			return null;
		}
		return loadedRules.get(index);
	}

}
