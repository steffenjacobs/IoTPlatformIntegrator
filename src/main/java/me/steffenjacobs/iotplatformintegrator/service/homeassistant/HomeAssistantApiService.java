package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiAvailabilityMessage;
import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiStatusMessage;
import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.HomeAssistantEvent;

/** @author Steffen Jacobs */
public class HomeAssistantApiService {

	private static final HomeAssistantSharedService sharedService = new HomeAssistantSharedService();

	public boolean isApiAvailable(String urlWithPort) throws ClientProtocolException, IOException {
		return 200 == sharedService.sendGet(urlWithPort, "").getStatusLine().getStatusCode();
	}

	public boolean isTokenCorrect(String urlWithPort, String bearerToken) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HttpResponse response = sharedService.sendGet(urlWithPort + "/api/", bearerToken);
		if (200 != response.getStatusLine().getStatusCode()) {
			return false;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ApiAvailabilityMessage msg = objectMapper.readValue(response.getEntity().getContent(), ApiAvailabilityMessage.class);
		return "API running.".equals(msg.getMessage());
	}

	public ApiStatusMessage getVersionInfo(String urlWithPort) throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HttpResponse response = sharedService.sendGet(urlWithPort + "/api/discovery_info", "");
		if (200 != response.getStatusLine().getStatusCode()) {
			throw new IOException("Error executing request, HTTP status: " + response.getStatusLine().getStatusCode());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(response.getEntity().getContent(), ApiStatusMessage.class);
	}

	public List<HomeAssistantEvent> getHomeAssistantEvents(String urlWithPort, String bearerToken)
			throws JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		HttpResponse response = sharedService.sendGet(urlWithPort + "/api/events", bearerToken);
		if (200 != response.getStatusLine().getStatusCode()) {
			throw new IOException("Error executing request, HTTP status: " + response.getStatusLine().getStatusCode());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(response.getEntity().getContent(), new TypeReference<List<HomeAssistantEvent>>() {
		});
	}
}
