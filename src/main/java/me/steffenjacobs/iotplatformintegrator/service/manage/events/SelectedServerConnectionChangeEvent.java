package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedServerConnectionChangeEvent extends WithServerConnectionEvent {

	public SelectedServerConnectionChangeEvent(ServerConnection selectedServerConnection) {
		super(EventType.SELECTED_SERVER_CONNECTION_CHANGED, selectedServerConnection);
	}
}
