package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.api.OpenHabApiStatusMessage;

/** @author Steffen Jacobs */
public class OpenHabApiService {

	public OpenHabApiStatusMessage getStatusMessage(String openHabUrlWithPort) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/"), new TypeReference<OpenHabApiStatusMessage>() {
		});
	}

}
