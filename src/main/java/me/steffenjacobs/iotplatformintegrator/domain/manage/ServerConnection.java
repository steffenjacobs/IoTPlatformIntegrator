package me.steffenjacobs.iotplatformintegrator.domain.manage;

/** @author Steffen Jacobs */
public class ServerConnection {

	public static enum PlatformType {
		OPENHAB, HOMEASSISTANT;
	}

	private final PlatformType platformType;
	private final String version;
	private final String instanceName;
	private final String url;
	private final int port;

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

}
