package me.steffenjacobs.iotplatformintegrator.service.shared;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public interface PlatformItemReverseTransformationAdapter<Item> {

	Item transformItem(SharedItem item);
}
