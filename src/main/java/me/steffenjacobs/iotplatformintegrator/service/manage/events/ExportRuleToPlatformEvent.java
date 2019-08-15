package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ExportRuleToPlatformEvent extends WithSharedRuleEvent {

	private final ServerConnection selectedConnection;
	private final String name;

	public ExportRuleToPlatformEvent(ServerConnection selectedConnection, SharedRule sharedRule, String name) {
		super(EventType.EXPORT_RULE_TO_PLATFORM, sharedRule);
		this.selectedConnection = selectedConnection;
		this.name = name;
	}

	public ServerConnection getSelectedServerConnection() {
		return selectedConnection;
	}

	public String getName() {
		return name;
	}

}
