package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class ItemFilterChangeEvent extends Event {

	private final String searchText;

	public ItemFilterChangeEvent(String searchText) {
		super(EventType.ITEM_FILTER_CHANGE);
		this.searchText = searchText;
	}
	
	public String getSearchText() {
		return searchText;
	}

}
