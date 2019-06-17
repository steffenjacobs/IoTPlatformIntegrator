package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiStatusMessage;
import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.HomeAssistantEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class TestHomeAssistantApiService {
	private static final String HOMEASSISTANT_URL_WITH_PORT = "http://192.168.1.24:8123";
	private static final SettingService settingService = new SettingService("./settings.config");

	@Test
	public void testIsTokenCorrect() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("Token is invalid. Did you specify a correct token via settings?",
				service.isTokenCorrect(HOMEASSISTANT_URL_WITH_PORT, settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
	}

	@Test
	public void testIsApiAvailable() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("API not available", service.isApiAvailable(HOMEASSISTANT_URL_WITH_PORT));
	}

	@Test
	public void testGetVersionInfo() throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		ApiStatusMessage versionInfo = service.getVersionInfo(HOMEASSISTANT_URL_WITH_PORT);
		Assert.assertEquals(HOMEASSISTANT_URL_WITH_PORT, versionInfo.getBaseUrl());
		Assert.assertEquals("Home", versionInfo.getLocationName());
		Assert.assertTrue(versionInfo.getRequiresApiPassword());
	}

	@Test
	public void testGetEvents() throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		List<HomeAssistantEvent> events = service.getHomeAssistantEvents(HOMEASSISTANT_URL_WITH_PORT, settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN));
		Assert.assertFalse(events.isEmpty());
		Assert.assertTrue(events.stream().filter(e -> e.getEvent().equals("homeassistant_start")).count() > 0);
	}
}
