package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.IOException;
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
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in.HomeAssistantItemTransformationService;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in.HomeAssistantManualRuleImporter;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerConnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerDisconnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.UrlUtil;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabApiService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabExperimentalRulesService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabItemService;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in.OpenHabCommandParser;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in.OpenHabTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.RuleValidator;

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

	private final Set<ServerConnection> currentConnections = new HashSet<>();
	private ServerConnection selectedConnection = null;

	private final PlatformTransformationAdapter<ItemDTO, ExperimentalRule> transformer = new OpenHabTransformationAdapter();

	public ImportPerspectiveController(SettingService settingService) {
		this.settingService = settingService;
		EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
			setSelectedServerConnection(((SelectedServerConnectionChangeEvent) e).getServerConnection());
		});
		EventBus.getInstance().addEventHandler(EventType.ServerDisconnected, e -> {
			removeServerConnection(((ServerDisconnectedEvent) e).getServerConnection());
		});
	}

	public void loadOpenHABRules(ServerConnection serverConnection) throws IOException {
		serverConnection.getRules().clear();
		final List<ExperimentalRule> retrievedRules = ruleService.requestAllRules(settingService.getSetting(SettingKey.OPENHAB_URI));
		LOG.info("Retrieved {} rules.", retrievedRules.size());
		for (ExperimentalRule rule : retrievedRules) {
			serverConnection.getRules().add(transformer.getRuleTransformer().transformRule(rule, serverConnection.getItemDirectory(), new OpenHabCommandParser()));
		}

		// avoid generating rule code with stale items
		EventBus.getInstance().fireEvent(new SelectedServerConnectionChangeEvent(serverConnection));
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
		EventBus.getInstance().fireEvent(new SelectedServerConnectionChangeEvent(serverConnection));
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
		EventBus.getInstance().fireEvent(new ServerConnectedEvent(serverConnection));

		Pair<List<SharedItem>, List<SharedRule>> itemsAndRules = haItemTransformationService
				.transformItemsAndRules(homeAssistantApiService.getAllState(urlWithPort, settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));

		serverConnection.getItemDirectory().addItems(itemsAndRules.getLeft());
		if (ruleValidator.containsEmptyRules(itemsAndRules.getRight())) {
			// TODO: merge with rules that were empty before
			serverConnection.getRules().addAll(ruleImporter.importRules(serverConnection.getItemDirectory()));
		} else {
			serverConnection.getRules().addAll(itemsAndRules.getRight());
		}

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

		EventBus.getInstance().fireEvent(new ServerConnectedEvent(connection));
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
		} else {
			clearSelection();
		}
	}

	private void removeServerConnection(ServerConnection serverConnection) {
		currentConnections.remove(serverConnection);
		clearSelection();
	}

	private void clearSelection() {
		selectedConnection = null;
	}
}
