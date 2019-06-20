package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerDisconnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabApiService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabCommandParser;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabItemService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.RuleValidator;
import me.steffenjacobs.iotplatformintegrator.ui.perspectives.ImportPerspective;
import me.steffenjacobs.iotplatformintegrator.ui.util.UrlUtil;

/** @author Steffen Jacobs */
public class ImportPerspectiveController {

	private static final Logger LOG = LoggerFactory.getLogger(ImportPerspectiveController.class);
	private static final OpenHabExperimentalRulesService ruleService = new OpenHabExperimentalRulesService();
	private static final OpenHabItemService itemService = new OpenHabItemService();
	private static final OpenHabApiService openHabApiService = new OpenHabApiService();

	private static final RuleValidator ruleValidator = new RuleValidator();
	private static final HomeAssistantManualRuleImporter ruleImporter = new HomeAssistantManualRuleImporter();

	private static final HomeAssistantApiService homeAssistantApiService = new HomeAssistantApiService();
	private static final HomeAssistantItemTransformationService haItemTransformationService = new HomeAssistantItemTransformationService();

	private final SettingService settingService;

	private ImportPerspective importPerspective;

	private final Set<ServerConnection> currentConnections = new HashSet<>();
	private ServerConnection selectedConnection = null;

	private final PlatformTransformationAdapter<ItemDTO, ExperimentalRule> transformer = new OpenHabTransformationAdapter();

	private SharedRule lastRule = null;

	public ImportPerspectiveController(SettingService settingService) {
		this.settingService = settingService;
		EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
			setSelectedServerConnection(((SelectedServerConnectionChangeEvent) e).getSelectedServerConnection());
		});
		EventBus.getInstance().addEventHandler(EventType.ServerDisconnected, e -> {
			removeServerConnection(((ServerDisconnectedEvent) e).getServerConnection());
		});
		EventBus.getInstance().addEventHandler(EventType.SelectedRuleChanged, e -> {
			lastRule = ((SelectedRuleChangedEvent) e).getSelectedRule();
		});
	}

	public void setImportPerspective(ImportPerspective importPerspective) {
		this.importPerspective = importPerspective;
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

	public void loadHomeAssistantData() throws ClientProtocolException, IOException {

		String urlWithPort = settingService.getSetting(SettingKey.HOMEASSISTANT_URI);
		ApiStatusMessage versionInfo = homeAssistantApiService.getVersionInfo(urlWithPort);

		Pair<String, Integer> parsedUrlAndPort = UrlUtil.parseUrlWithPort(urlWithPort);

		ServerConnection serverConnection = new ServerConnection(ServerConnection.PlatformType.HOMEASSISTANT, versionInfo.getVersion(), versionInfo.getLocationName(),
				parsedUrlAndPort.getLeft(), parsedUrlAndPort.getRight());
		currentConnections.add(serverConnection);
		importPerspective.onConnectionEstablished(serverConnection);

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
		EventBus.getInstance().fireEvent(new SelectedServerConnectionChangeEvent(serverConnection));
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

		importPerspective.onConnectionEstablished(connection);
		loadOpenHABItems(connection);
		loadOpenHABRules(connection);

		EventBus.getInstance().fireEvent(new SelectedServerConnectionChangeEvent(connection));
	}

	private void setSelectedServerConnection(ServerConnection serverConnection) {
		if (selectedConnection == serverConnection) {
			return;
		}

		if (serverConnection != null) {
			selectedConnection = serverConnection;
			importPerspective.refreshItems(selectedConnection.getItemDirectory().getAllItems());
			importPerspective.refreshRulesTable(selectedConnection.getRules());
			importPerspective.resetCodeEditor();
		} else {
			clearSelection();
		}
	}

	private void removeServerConnection(ServerConnection serverConnection) {
		currentConnections.remove(serverConnection);
		// TODO: use event system
		importPerspective.propagateRemovalOfServerConnection(serverConnection);
		clearSelection();
	}

	private void clearSelection() {
		selectedConnection = null;
		importPerspective.refreshItems(new ArrayList<SharedItem>());
		importPerspective.refreshRulesTable(new ArrayList<SharedRule>());
		importPerspective.resetCodeEditor();
	}

	public SharedRule getLastSelectedRule() {
		return lastRule;
	}

}
