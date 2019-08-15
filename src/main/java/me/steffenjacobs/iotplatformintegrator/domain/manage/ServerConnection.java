package me.steffenjacobs.iotplatformintegrator.domain.manage;

import java.util.ArrayList;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class ServerConnection {

	public static enum PlatformType {
		OPENHAB, HOMEASSISTANT, MONGO;
	}

	private final PlatformType platformType;
	private final String version;
	private final String instanceName;
	private final String url;
	private final int port;

	private final ItemDirectory itemDirectory = new ItemDirectory();
	private final List<SharedRule> rules = new ArrayList<>();

	public ServerConnection(PlatformType platformType, String version, String instanceName, String url, int port) {
		super();
		this.platformType = platformType;
		this.version = version;
		this.instanceName = instanceName;
		this.url = url;
		this.port = port;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public PlatformType getPlatformType() {
		return platformType;
	}

	public String getVersion() {
		return version;
	}

	public String getUrl() {
		return url;
	}

	public int getPort() {
		return port;
	}

	public ItemDirectory getItemDirectory() {
		return itemDirectory;
	}

	public List<SharedRule> getRules() {
		return rules;
	}

	@Override
	public String toString() {
		return instanceName;
	}

}
