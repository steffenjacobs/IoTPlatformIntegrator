package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;

/** @author Steffen Jacobs */
public class RuleController {

	private SharedRule lastRule = null;
	private ServerConnection currentConnection = null;

	public RuleController() {
		EventBus.getInstance().addEventHandler(EventType.SelectedRuleChanged, e -> {
			lastRule = ((SelectedRuleChangeEvent) e).getSelectedRule();
		});
		EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
			currentConnection = ((SelectedServerConnectionChangeEvent) e).getServerConnection();
			lastRule = null;
		});
	}

	public SharedRule getLastSelectedRule() {
		return lastRule;
	}

	public SharedRule getRuleByIndex(int index) {
		if (currentConnection == null) {
			// TODO
		}
		if (index < 0 || index >= currentConnection.getRules().size()) {
			return null;
		}
		return currentConnection.getRules().get(index);
	}

	public SharedRule getRuleByIndex(ServerConnection serverConnection, int index) {
		if (index < 0 || index >= serverConnection.getRules().size()) {
			return null;
		}
		return serverConnection.getRules().get(index);
	}
}
