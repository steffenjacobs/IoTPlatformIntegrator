package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.creation.ItemCreationDTO;

/** @author Steffen Jacobs */
public final class OpenHabItemService {

	private final OpenHabSharedService sharedService = new OpenHabSharedService();

	public List<ItemDTO> requestItems(String openHabUrlWithPort) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/items?recursive=true"), new TypeReference<List<ItemDTO>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public Optional<ItemDTO> requestItemByName(String openHabUrlWithPort, String itemname) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return Optional.of(objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/items/" + itemname), new TypeReference<ItemDTO>() {
			}));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(null);
	}

	public int createItem(String openHabUrlWithPort, ItemCreationDTO item) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String itemJson = "[" + objectMapper.writeValueAsString(item) + "]";
			
			return sharedService.sendPutWithPathParameters(openHabUrlWithPort + "/rest/items", itemJson);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int createItemByName(String openHabUrlWithPort, ItemCreationDTO item, Optional<String> itemname) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String itemJson = objectMapper.writeValueAsString(item);
			System.out.println(itemname.map(val -> "/"+ val).orElse(""));
			return sharedService.sendPutWithPathParameters(openHabUrlWithPort + "/rest/items" + itemname.map(val -> "/"+ val).orElse(""), itemJson);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public int postItem(String openHabUrlWithPort, String itemname, String body){
		
		try {
			return sharedService.sendPost(openHabUrlWithPort + "/rest/items/" + itemname, false, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
		
	}	
	
	public boolean deleteItem(String openHabUrlwithPort, String itemname) {
		try {
			return sharedService.sendDelete(openHabUrlwithPort + "/rest/items/" + itemname);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getItemState(String openHabUrlwithPort, String itemname) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(openHabUrlwithPort + "/rest/items/" + itemname + "/state");
		

		
		HttpResponse response;
		try {
			response = client.execute(get);
			System.out.println(response.getStatusLine().getStatusCode());
			
			BasicResponseHandler basicResponseHandler = new BasicResponseHandler();
			
			String handleResponse = basicResponseHandler.handleResponse(response);
			
			return handleResponse;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	
	}
	
	public int updateItemState(String openHabUrlwithPort, String itemname, String body) {
		try {
			return sharedService.sendPost(openHabUrlwithPort + "/rest/items/" + itemname, false, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
