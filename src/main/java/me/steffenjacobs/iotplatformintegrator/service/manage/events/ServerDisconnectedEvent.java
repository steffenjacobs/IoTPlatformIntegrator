package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ServerDisconnectedEvent extends Event {

	private final ServerConnection serverConnection;

	public ServerDisconnectedEvent(ServerConnection serverConnection) {
		super(EventType.ServerDisconnected);
		this.serverConnection = serverConnection;
	}

	public ServerConnection getServerConnection() {
		return serverConnection;
	}

}
