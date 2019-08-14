package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ServerDisconnectedEvent extends WithServerConnectionEvent {

	public ServerDisconnectedEvent(ServerConnection serverConnection) {
		super(EventType.SERVER_DISCONNECTED, serverConnection);
	}
}
