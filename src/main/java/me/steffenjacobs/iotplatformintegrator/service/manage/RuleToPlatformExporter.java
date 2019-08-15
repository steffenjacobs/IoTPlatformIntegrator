package me.steffenjacobs.iotplatformintegrator.service.manage;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ExportRuleToPlatformEvent;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out.OpenHabCommandReverseTransformer;
import me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out.OpenHabReverseTransformationAdapter;

/** @author Steffen Jacobs */
public class RuleToPlatformExporter {

	private final OpenHabReverseTransformationAdapter openHabReverseTransformationAdapter;
	private final OpenHabCommandReverseTransformer openHabCommandReverseTransformer;

	public RuleToPlatformExporter() {
		EventBus.getInstance().addEventHandler(EventType.EXPORT_RULE_TO_PLATFORM, e -> export(((ExportRuleToPlatformEvent) e).getSelectedServerConnection(),
				((ExportRuleToPlatformEvent) e).getSelectedRule(), ((ExportRuleToPlatformEvent) e).getName()));

		openHabReverseTransformationAdapter = new OpenHabReverseTransformationAdapter();
		openHabCommandReverseTransformer = new OpenHabCommandReverseTransformer();
	}

	private void export(ServerConnection serverConnection, SharedRule rule, String name) {
		if (serverConnection.getPlatformType() == PlatformType.OPENHAB) {
			final ExperimentalRule openHabRule = openHabReverseTransformationAdapter.getRuleTransformer().transformRule(rule, openHabCommandReverseTransformer);
			// TODO: handle items
		}
		// TODO: handle export of home assistant rules + items

	}

}
