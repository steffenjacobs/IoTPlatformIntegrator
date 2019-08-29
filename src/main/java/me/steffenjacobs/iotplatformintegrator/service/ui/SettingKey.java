package me.steffenjacobs.iotplatformintegrator.service.ui;

/** @author Steffen Jacobs */
public enum SettingKey {
	OPENHAB_URI("http://localhost:8080", "openhab-uri", "OpenHAB URI"), //
	SHOW_WHITESPACES("1", "show-whitespaces", "Show Whitespaces"), //
	FORMAT_CODE("1", "format-code", "Format Pseudocode"), //
	HOMEASSISTANT_API_TOKEN("<please specify>", "ha-api-token", "Home Assistant API Token"), //
	HOMEASSISTANT_URI("http://192.168.1.24:8123", "homeassistant-uri", "HomeAssistant URI"), //
	HOMEASSISTANT_FILE_URI("C:\\Users\\Steffen\\Dropbox\\Masterarbeit", "homeassistant-file-uri", "HomeAssistant URI to automation file"), //
	USERNAME("user", "username", "Username"), //
	PASSWORD("changeme", "password", "Password"), //
	USERID("1337", "userid", "UserId"), //
	DATABASE_URI("mongodb://localhost", "database-uri", "Database URI"),//
	BABELNET_API_KEY("5f7d9a98-c99f-454e-a9b5-4f17305e7845", "babelnet-api-key", "Babelnet API Key");

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
