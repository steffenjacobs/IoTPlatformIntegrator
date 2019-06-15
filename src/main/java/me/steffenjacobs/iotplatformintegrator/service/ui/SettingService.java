package me.steffenjacobs.iotplatformintegrator.service.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public class SettingService {

	private static final Logger LOG = LoggerFactory.getLogger(SettingService.class);

	private Properties settings;
	private final String path;

	public SettingService(String path) {
		this.path = path;
		loadOrCreateSettings(path);
	}

	private void loadOrCreateSettings(String path) {
		try {
			loadSettings(path);
		} catch (IOException e) {
			settings = new Properties();
		}
	}

	private void loadSettings(String path) throws FileNotFoundException, IOException {
		settings = new Properties();
		File file = new File(path);
		settings.load(new FileInputStream(file));
		LOG.info("Loaded settings from {}", path);
	}

	private void saveSettings() {
		try {
			settings.store(new FileOutputStream(path), "");
			LOG.info("Saved settings to {}", path);
		} catch (IOException e) {
			LOG.error("Could not store setting: " + e.getMessage(), e);
			// TODO: proper exception handling
		}
	}

	public String getSetting(SettingKey key) {
		return settings.getProperty(key.getConfigKey(), key.getDefaultValue());
	}

	public String setSetting(SettingKey key, String value) {
		Object res = settings.setProperty(key.getConfigKey(), value);
		saveSettings();
		if (res instanceof String) {
			return (String) res;
		}
		return null;
	}

}
