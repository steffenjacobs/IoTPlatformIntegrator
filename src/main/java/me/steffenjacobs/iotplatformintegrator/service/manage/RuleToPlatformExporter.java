package me.steffenjacobs.iotplatformintegrator.service.manage;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ExportRuleToPlatformEvent;

/** @author Steffen Jacobs */
public class RuleToPlatformExporter {

	public RuleToPlatformExporter() {
		EventBus.getInstance().addEventHandler(EventType.EXPORT_RULE_TO_PLATFORM, e -> export(((ExportRuleToPlatformEvent) e).getSelectedServerConnection(),
				((ExportRuleToPlatformEvent) e).getSelectedRule(), ((ExportRuleToPlatformEvent) e).getName()));
	}

	private void export(ServerConnection serverConnection, SharedRule rule, String name) {

	}

}
