package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.out.HomeAssistantManualRuleExporter;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ExportRuleToPlatformEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RefreshOpenHABDataEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out.OpenHabCommandReverseTransformer;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out.OpenHabReverseTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.ui.ServerConnectionManager;

/** @author Steffen Jacobs */
public class RuleToPlatformExporter {

	private final OpenHabReverseTransformationAdapter openHabReverseTransformationAdapter;
	private final OpenHabCommandReverseTransformer openHabCommandReverseTransformer;

	private final HomeAssistantManualRuleExporter homeAssistantRuleExporter;

	public RuleToPlatformExporter() {
		EventBus.getInstance().addEventHandler(EventType.EXPORT_RULE_TO_PLATFORM, e -> export(((ExportRuleToPlatformEvent) e).getSelectedServerConnection(),
				((ExportRuleToPlatformEvent) e).getSelectedRule(), ((ExportRuleToPlatformEvent) e).getName()));

		openHabReverseTransformationAdapter = new OpenHabReverseTransformationAdapter();
		openHabCommandReverseTransformer = new OpenHabCommandReverseTransformer();
		homeAssistantRuleExporter = new HomeAssistantManualRuleExporter();
	}

	private void export(ServerConnection serverConnection, SharedRule rule, String name) {
		if (serverConnection.getPlatformType() == PlatformType.OPENHAB) {
			final ExperimentalRule openHabRule = openHabReverseTransformationAdapter.getRuleTransformer().transformRule(rule, openHabCommandReverseTransformer);
			ServerConnectionManager.getRuleservice().createRule(serverConnection.getUrl() + ":" + serverConnection.getPort(), openHabRule);
			EventBus.getInstance().fireEvent(new RefreshOpenHABDataEvent());
			EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(App.getRemoteRuleCache().getRuleByName(rule.getName())));
		}
		if (serverConnection.getPlatformType() == PlatformType.HOMEASSISTANT) {
			final JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showSaveDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				final File selectedFile = jfc.getSelectedFile();
				homeAssistantRuleExporter.exportRule(rule, selectedFile);
				EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(App.getRemoteRuleCache().getRuleByName(rule.getName())));
			}
		}
	}

}
