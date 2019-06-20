package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class WithServerConnectionEvent extends Event {

	private final ServerConnection serverConnection;

	protected WithServerConnectionEvent(EventType eventType, ServerConnection serverConnection) {
		super(eventType);
		this.serverConnection = serverConnection;
	}

	public ServerConnection getServerConnection() {
		return serverConnection;
	}
}
