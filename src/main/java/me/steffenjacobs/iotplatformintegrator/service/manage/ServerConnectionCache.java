package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.HashSet;
import java.util.Set;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithServerConnectionEvent;

/** @author Steffen Jacobs */
public class ServerConnectionCache {

	private final Set<ServerConnection> connections = new HashSet<>();

	public ServerConnectionCache() {

		EventBus.getInstance().addEventHandler(EventType.SERVER_CONNECTED, e -> connections.add(((WithServerConnectionEvent) e).getServerConnection()));
		EventBus.getInstance().addEventHandler(EventType.SERVER_DISCONNECTED, e -> connections.remove(((WithServerConnectionEvent) e).getServerConnection()));
	}

	public Set<ServerConnection> getConnections() {
		return connections;
	}
}
