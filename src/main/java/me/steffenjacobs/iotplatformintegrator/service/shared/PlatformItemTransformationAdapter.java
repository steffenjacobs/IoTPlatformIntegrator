package me.steffenjacobs.iotplatformintegrator.service.shared;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

/** @author Steffen Jacobs */
public interface PlatformItemTransformationAdapter<Item> {

	SharedItem transformItem(Item rule);
}