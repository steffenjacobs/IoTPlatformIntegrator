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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + port;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerConnection other = (ServerConnection) obj;
		if (port != other.port)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	

}
