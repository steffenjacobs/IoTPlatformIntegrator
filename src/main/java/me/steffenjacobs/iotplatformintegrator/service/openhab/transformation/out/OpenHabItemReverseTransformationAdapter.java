package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformItemReverseTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabItemReverseTransformationAdapter implements PlatformItemReverseTransformationAdapter<ItemDTO> {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHabItemReverseTransformationAdapter.class);

	@Override
	public ItemDTO transformItem(SharedItem item) {
		ItemDTO result = new ItemDTO();
		result.setName(item.getName());
		result.setLabel(item.getLabel());
		result.setType(getItemType(item.getType()));
		LOG.info("Reverse transformed item {}.", item.getName());
		return result;
	}

	private String getItemType(ItemType itemType) {
		switch (itemType) {
		case Switch:
			return "Switch";
		case Number:
			return "Number";
		case String:
			return "String";
		case Player:
			return "Player";
		case Color:
			return "Color";
		case Contact:
			return "Contact";
		case Dimmer:
			return "Dimmer";
		case Rollershutter:
			return "Rollershutter";
		case Image:
			return "Image";
		case Location:
			return "Location";
		case DateTime:
			return "DateTime";
		case Group:
			return "Group";
		default:
			LOG.warn("Could not reverse transform item type: {}", itemType.name());
			return "";
		}
	}
}
