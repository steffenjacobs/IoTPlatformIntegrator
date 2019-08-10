package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.HashSet;
import java.util.Set;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public class ItemDirectoryHolder {

	private final static ItemDirectoryHolder instance = new ItemDirectoryHolder();

	private final Set<ServerConnection> connections = new HashSet<>();

	public static ItemDirectoryHolder getInstance() {
		return instance;
	}

	public void add(ServerConnection connection) {
		this.connections.add(connection);
	}

	public ServerConnection getServerConnection(SharedItem i) {
		for (ServerConnection c : connections) {
			SharedItem item = c.getItemDirectory().getItemByName(i.getName());
			if (item != null && item == i) {
				return c;
			}
		}
		return new ServerConnection(PlatformType.MONGO, "", "<UNKNWON SOURCE", "", -1);
	}

}
