package me.steffenjacobs.iotplatformintegrator.service.ui;

/** @author Steffen Jacobs */
public enum SettingKey {
	OPENHAB_URI("http://localhost:8080", "openhab-uri", "OpenHAB URI"),
	SHOW_WHITESPACES("1", "show-whitespaces", "Show Whitespaces"), //
	FORMAT_CODE("1", "format-code", "Format Pseudocode"),
	HOMEASSISTANT_API_TOKEN("<please specify>", "ha-api-token", "Home Assistant API Token"), //
	HOMEASSISTANT_URI("http://192.168.1.24:8123", "homeassistant-uri", "HomeAssistant URI"), //
	HOMEASSISTANT_FILE_URI("C:\\Users\\Steffen\\Dropbox\\Masterarbeit", "homeassistant-file-uri",
			"HomeAssistant URI to automation file");

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
