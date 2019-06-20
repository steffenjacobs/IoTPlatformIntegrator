package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ServerConnectedEvent extends WithServerConnectionEvent {

	public ServerConnectedEvent(ServerConnection serverConnection) {
		super(EventType.ServerConnected, serverConnection);
	}
}
