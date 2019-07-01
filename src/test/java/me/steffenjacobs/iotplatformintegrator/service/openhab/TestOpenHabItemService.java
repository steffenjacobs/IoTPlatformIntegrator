package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.creation.ItemCreationDTO;

public class TestOpenHabItemService {

	@Test
	public void test() {
		OpenHabItemService service = new OpenHabItemService();

		List<ItemDTO> requestItems1 = service.requestItems("http://localhost:8080");

		ItemCreationDTO itemCreationDTO = new ItemCreationDTO();
		itemCreationDTO.setName("TestName");
		itemCreationDTO.setLabel("TestLabel");
		itemCreationDTO.setType("Switch");
		int createItem = service.createItem("http://localhost:8080", itemCreationDTO);
		List<ItemDTO> requestItems2 = service.requestItems("http://localhost:8080");

		Assert.assertEquals(200, createItem);
		Assert.assertTrue(requestItems1.size() + 1 == requestItems2.size());

		ItemCreationDTO itemCreationDTO1 = new ItemCreationDTO();
		itemCreationDTO1.setName("TestName1");
		itemCreationDTO1.setLabel("TestLabel1");
		itemCreationDTO1.setType("Switch");

		int createItem2 = service.createItemByName("http://localhost:8080", itemCreationDTO1, Optional.ofNullable("ka"));
		Assert.assertEquals(createItem2, 201);

		List<ItemDTO> requestItems3 = service.requestItems("http://localhost:8080");
		Assert.assertTrue(requestItems2.size() + 1 == requestItems3.size());

		// List<ItemDTO> requestItems2 = service.requestItems("http://localhost:8080");

		Optional<ItemDTO> requestItemByName = new OpenHabItemService().requestItemByName("http://localhost:8080", "TestName1");

		Assert.assertTrue(requestItemByName.isPresent());

		service.postItem("http://localhost:8080", "TestName1", "ON");

		String itemState = service.getItemState("http://localhost:8080", "TestName1");
		Assert.assertTrue(itemState.equals("ON"));
		service.updateItemState("http://localhost:8080", "TestName1", "OFF");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		itemState = service.getItemState("http://localhost:8080", "TestName1");

		Assert.assertTrue(itemState.equals("OFF"));

		service.deleteItem("http://localhost:8080", "TestName1");
		service.deleteItem("http://localhost:8080", "TestName");

		List<ItemDTO> requestItems4 = service.requestItems("http://localhost:8080");

		Assert.assertTrue(requestItems4.size() + 2 == requestItems3.size());

	}

}
