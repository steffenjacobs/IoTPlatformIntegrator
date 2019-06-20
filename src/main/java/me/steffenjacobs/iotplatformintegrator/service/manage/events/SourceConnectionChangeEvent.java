package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class SourceConnectionChangeEvent extends WithServerConnectionEvent {

	public SourceConnectionChangeEvent(ServerConnection selectedServerConnection) {
		super(EventType.SourceConnectionChanged, selectedServerConnection);
	}
}
