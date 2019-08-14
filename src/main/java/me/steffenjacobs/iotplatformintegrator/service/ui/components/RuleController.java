package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteItemAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleTableHolder.RuleTableHolderType;

/** @author Steffen Jacobs */
public class RuleController {

	private SharedRule lastRule = null;
	private ServerConnection currentConnection = null;

	public RuleController(RuleTableHolderType type) {
		switch (type) {
		case Default:
		case Source:
		case Target:
			EventBus.getInstance().addEventHandler(EventType.SELECTED_RULE_CHANGE, e -> lastRule = ((SelectedRuleChangeEvent) e).getSelectedRule());
			EventBus.getInstance().addEventHandler(EventType.SELECTED_SERVER_CONNECTION_CHANGED, e -> {
				currentConnection = ((SelectedServerConnectionChangeEvent) e).getServerConnection();
				lastRule = null;
			});
			break;
		case Remote:
			EventBus.getInstance().addEventHandler(EventType.REMOTE_RULE_CHANGE, e -> lastRule = ((WithSharedRuleEvent) e).getSelectedRule());
			EventBus.getInstance().addEventHandler(EventType.CLEAR_ALL_REMOTE_RULES, e -> {
				lastRule = null;
				currentConnection.getRules().clear();
			});
			EventBus.getInstance().addEventHandler(EventType.REMOTE_RULE_ADDED, e -> currentConnection.getRules().add(((WithSharedRuleEvent) e).getSelectedRule()));
			EventBus.getInstance().addEventHandler(EventType.REMOTE_ITEM_ADDED, e -> currentConnection.getItemDirectory().addItem(((RemoteItemAddedEvent) e).getItem()));
			EventBus.getInstance().addEventHandler(EventType.CLEAR_ALL_REMOTE_ITEMS, e -> currentConnection.getItemDirectory().clearItems());
			break;
		}
	}

	public void setMockConnection(ServerConnection currentConnection) {
		this.currentConnection = currentConnection;
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
