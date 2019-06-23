package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public interface ItemPlaceholderFactory {
	default SharedItem getItemOrPlaceholder(Object item) {
		if (item instanceof SharedItem) {
			return (SharedItem) item;
		} else if (item instanceof String) {
			return new SharedItem("<invalid item name '" + item + "'>", "<invalid item name '" + item + "'>", ItemType.Unknown);
		} else {
			return new SharedItem("<null item>", "<null item>", ItemType.Unknown);
		}
	}
}
