package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiStatusMessage;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.api.OpenHabApiStatusMessage;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.HomeAssistantApiService;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.HomeAssistantItemTransformationService;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.HomeAssistantManualRuleImporter;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabApiService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabCommandParser;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabItemService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.RuleValidator;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController;
import me.steffenjacobs.iotplatformintegrator.ui.UiEntrypoint;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;
import me.steffenjacobs.iotplatformintegrator.ui.util.UrlUtil;

/** @author Steffen Jacobs */
public class UiEntrypointController {

	private static final Logger LOG = LoggerFactory.getLogger(UiEntrypointController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();
	private static final OpenHabItemService itemService = new OpenHabItemService();
	private static final OpenHabApiService openHabApiService = new OpenHabApiService();

	private static final RuleValidator ruleValidator = new RuleValidator();
	private static final HomeAssistantManualRuleImporter ruleImporter = new HomeAssistantManualRuleImporter();

	private static final HomeAssistantApiService homeAssistantApiService = new HomeAssistantApiService();
	private static final HomeAssistantItemTransformationService haItemTransformationService = new HomeAssistantItemTransformationService();

	private final SettingService settingService;
	private UiEntrypoint ui;

	private final List<ServerConnection> currentConnections = new ArrayList<>();
	private ServerConnection selectedConnection = null;

	private final PlatformTransformationAdapter<ItemDTO, ExperimentalRule> transformer = new OpenHabTransformationAdapter();
	private final CodeEditorController codeEditorController;

	private SharedRule lastRule = null;

	public UiEntrypointController(SettingService settingService, CodeEditor codeEditor) {
		this.settingService = settingService;
		codeEditorController = new CodeEditorController(codeEditor, settingService);
		codeEditor.setController(codeEditorController);
	}

	public CodeEditorController getCodeEditorController() {
		return codeEditorController;
	}

	public void setUi(UiEntrypoint ui) {
		this.ui = ui;
	}

	public void loadOpenHABRules(ServerConnection serverConnection) throws IOException {
		serverConnection.getRules().clear();
		final List<ExperimentalRule> retrievedRules = ruleService.requestAllRules(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} rules.", retrievedRules.size());
		for (ExperimentalRule rule : retrievedRules) {
			serverConnection.getRules().add(transformer.getRuleTransformer().transformRule(rule, serverConnection.getItemDirectory(), new OpenHabCommandParser()));
		}
		lastRule = null;
	}

	public void loadOpenHABItems(ServerConnection serverConnection) throws IOException {

		serverConnection.getItemDirectory().clearItems();
		final List<ItemDTO> retrievedItems = itemService.requestItems(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} items.", retrievedItems.size());
		for (ItemDTO item : retrievedItems) {
			SharedItem transformedItem = transformer.getItemTransformer().transformItem(item);
			serverConnection.getItemDirectory().addItem(transformedItem);
		}

		// avoid generating rule code with stale items
		lastRule = null;
	}

	public SharedRule getRuleByIndex(int index) {
		if (selectedConnection == null) {
			// TODO
		}
		if (index < 0 || index >= selectedConnection.getRules().size()) {
			return null;
		}
		return selectedConnection.getRules().get(index);
	}

	public SharedRule getRuleByIndex(ServerConnection serverConnection, int index) {
		if (index < 0 || index >= serverConnection.getRules().size()) {
			return null;
		}
		return serverConnection.getRules().get(index);
	}

	public String getOHUrlWithPort() {
		return settingService.getSetting(SettingKey.OPENHAB_URI);
	}

	public void renderPseudocode(SharedRule rule) {
		if (lastRule != rule) {
			codeEditorController.renderPseudocode(rule);
			lastRule = rule;
		}

	}

	public void loadHomeAssistantData() throws ClientProtocolException, IOException {

		String urlWithPort = settingService.getSetting(SettingKey.HOMEASSISTANT_URI);
		ApiStatusMessage versionInfo = homeAssistantApiService.getVersionInfo(urlWithPort);

		Pair<String, Integer> parsedUrlAndPort = UrlUtil.parseUrlWithPort(urlWithPort);

		ServerConnection serverConnection = new ServerConnection(ServerConnection.PlatformType.HOMEASSISTANT, versionInfo.getVersion(), versionInfo.getLocationName(),
				parsedUrlAndPort.getLeft(), parsedUrlAndPort.getRight());
		currentConnections.add(serverConnection);
		ui.onConnectionEstablished(serverConnection);

		Pair<List<SharedItem>, List<SharedRule>> itemsAndRules = haItemTransformationService
				.transformItemsAndRules(homeAssistantApiService.getAllState(urlWithPort, settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));

		serverConnection.getItemDirectory().addItems(itemsAndRules.getLeft());
		if (ruleValidator.containsEmptyRules(itemsAndRules.getRight())) {
			// TODO: merge with rules that were empty before
			serverConnection.getRules().addAll(ruleImporter.importRules(serverConnection.getItemDirectory()));
		} else {
			serverConnection.getRules().addAll(itemsAndRules.getRight());
		}

		lastRule = null;
		setSelectedServerConnection(serverConnection);
	}

	public Object getHAUrlWithPort() {
		return settingService.getSetting(SettingKey.HOMEASSISTANT_URI);
	}

	public void loadOpenHABData() throws IOException {
		OpenHabApiStatusMessage statusMessage = openHabApiService.getStatusMessage(getOHUrlWithPort());

		String urlWithPort = settingService.getSetting(SettingKey.OPENHAB_URI);
		Pair<String, Integer> parsedUrlAndPort = UrlUtil.parseUrlWithPort(urlWithPort);
		ServerConnection connection = new ServerConnection(PlatformType.OPENHAB, statusMessage.getVersion(), "Open Hab Instance on port: " + parsedUrlAndPort.getRight(),
				parsedUrlAndPort.getLeft(), parsedUrlAndPort.getRight());
		currentConnections.add(connection);

		ui.onConnectionEstablished(connection);
		loadOpenHABItems(connection);
		loadOpenHABRules(connection);

		setSelectedServerConnection(connection);
	}

	public ServerConnection getSelectedServerConnection() {
		return selectedConnection;
	}

	public void setSelectedServerConnection(ServerConnection serverConnection) {
		selectedConnection = serverConnection;
		ui.refreshItems(selectedConnection.getItemDirectory().getAllItems());
		ui.refreshRulesTable(selectedConnection.getRules());
		ui.resetCodeEditor();
	}

}
