package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SelectedServerConnectionChangeEvent extends Event {

	private final ServerConnection selectedServerConnection;

	public SelectedServerConnectionChangeEvent(ServerConnection selectedServerConnection) {
		super(EventType.SelectedServerConnectionChanged);
		this.selectedServerConnection = selectedServerConnection;
	}

	public ServerConnection getSelectedServerConnection() {
		return selectedServerConnection;
	}

}
