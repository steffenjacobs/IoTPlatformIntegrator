package me.steffenjacobs.iotplatformintegrator.service.openhab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformItemTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabItemTransformationAdapter implements PlatformItemTransformationAdapter<ItemDTO> {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHabItemTransformationAdapter.class);

	@Override
	public SharedItem transformItem(ItemDTO item) {
		String name = item.getName();
		String label = item.getLabel();
		ItemType type = getItemType(item.getType());
		LOG.info("Transformed item {}.", name);
		return new SharedItem(name, label, type);
	}

	private ItemType getItemType(String itemType) {
		switch (itemType) {
		case "Switch":
			return ItemType.Switch;
		case "Number":
			return ItemType.Number;
		case "String":
			return ItemType.String;
		case "Player":
			return ItemType.Player;
		case "Color":
			return ItemType.Color;
		case "Contact":
			return ItemType.Contact;
		case "Dimmer":
			return ItemType.Dimmer;
		case "Rollershutter":
			return ItemType.Rollershutter;
		case "Image":
			return ItemType.Image;
		case "Location":
			return ItemType.Location;
		case "DateTime":
			return ItemType.DateTime;
		case "Group":
			// TODO: handle group
			return ItemType.Group;
		default:
			return ItemType.Unknown;
		}
	}
}
