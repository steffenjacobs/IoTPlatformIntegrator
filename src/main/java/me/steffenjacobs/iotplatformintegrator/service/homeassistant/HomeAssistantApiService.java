package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.iotplatformintegrator.domain.homeassistant.ApiAvailabilityMessage;

/** @author Steffen Jacobs */
public class HomeAssistantApiService {

	private static final HomeAssistantSharedService sharedService = new HomeAssistantSharedService();

	public boolean isApiAvailable(String urlWithPort, String bearerToken) throws ClientProtocolException, IOException {
		return 200 == sharedService.sendGet(urlWithPort, bearerToken).getStatusLine().getStatusCode();
	}

	public boolean isTokenCorrect(String urlWithPort, String bearerToken) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		HttpResponse response = sharedService.sendGet(urlWithPort + "/api/", bearerToken);
		ObjectMapper objectMapper = new ObjectMapper();
		if (200 != response.getStatusLine().getStatusCode()) {
			return false;
		}
		ApiAvailabilityMessage msg = objectMapper.readValue(response.getEntity().getContent(), ApiAvailabilityMessage.class);
		return "API running.".equals(msg.getMessage());
	}
	// public void getVersionInfo(String urlWithPort, String bearerToken) {
	// sharedService.sendGet(urlWithPort, bearerToken)
	// }
}
