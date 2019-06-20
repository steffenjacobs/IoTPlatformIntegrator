package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class TargetConnectionChangeEvent extends WithServerConnectionEvent {

	public TargetConnectionChangeEvent(ServerConnection selectedServerConnection) {
		super(EventType.TargetConnectionChanged, selectedServerConnection);
	}
}
