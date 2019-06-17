package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class TestHomeAssistantApiService {
	private static final SettingService settingService = new SettingService("./settings.config");

	@Test
	public void testIsTokenCorrect() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("Token is invalid. Did you specify a correct token via settings?",
				service.isTokenCorrect("http://192.168.1.24:8123", settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
	}

	@Test
	public void testIsApiAvailable() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("API not available", service.isApiAvailable("http://192.168.1.24:8123", "invalid-token"));
	}
}
