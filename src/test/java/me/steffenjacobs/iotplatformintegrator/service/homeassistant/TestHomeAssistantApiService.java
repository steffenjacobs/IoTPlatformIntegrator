package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiStatusMessage;
import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.HomeAssistantEvent;
import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.states.State;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class TestHomeAssistantApiService {
	private static final SettingService settingService = new SettingService("./settings.config");

	@Test
	public void testIsTokenCorrect() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("Token is invalid. Did you specify a correct token via settings?",
				service.isTokenCorrect(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
	}

	@Test
	public void testIsApiAvailable() throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue("API not available", service.isApiAvailable(settingService.getSetting(SettingKey.HOMEASSISTANT_URI)));
	}

	@Test
	public void testGetVersionInfo() throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		ApiStatusMessage versionInfo = service.getVersionInfo(settingService.getSetting(SettingKey.HOMEASSISTANT_URI));
		Assert.assertEquals(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), versionInfo.getBaseUrl());
		Assert.assertEquals("Home", versionInfo.getLocationName());
		Assert.assertTrue(versionInfo.getRequiresApiPassword());
	}

	@Test
	public void testGetEvents() throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		List<HomeAssistantEvent> events = service.getHomeAssistantEvents(settingService.getSetting(SettingKey.HOMEASSISTANT_URI),
				settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN));
		Assert.assertFalse(events.isEmpty());
		Assert.assertTrue(events.stream().filter(e -> e.getEvent().equals("homeassistant_start")).count() > 0);
	}

	@Test
	public void testApiValid() throws IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		Assert.assertTrue(service.validateConfiguration(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN)));
	}

	@Test
	public void testGetAllState() throws ClientProtocolException, IOException {
		HomeAssistantApiService service = new HomeAssistantApiService();
		List<State> allState = service.getAllState(settingService.getSetting(SettingKey.HOMEASSISTANT_URI), settingService.getSetting(SettingKey.HOMEASSISTANT_API_TOKEN));
		Assert.assertFalse(allState.isEmpty());
	}
}
