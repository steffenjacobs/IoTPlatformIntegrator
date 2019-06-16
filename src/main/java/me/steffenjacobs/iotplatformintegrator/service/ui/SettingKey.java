package me.steffenjacobs.iotplatformintegrator.service.ui;

/** @author Steffen Jacobs */
public enum SettingKey {
	OPENHAB_URI("http://localhost:8080", "openhab-uri", "OpenHAB URI"), SHOW_WHITESPACES("1", "show-whitespaces", "Show Whitespaces");
	private final String defaultValue;
	private final String configKey;
	private final String title;

	private SettingKey(String defaultValue, String configKey, String title) {
		this.defaultValue = defaultValue;
		this.configKey = configKey;
		this.title = title;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getConfigKey() {
		return configKey;
	}

	public String getTitle() {
		return title;
	}
}
