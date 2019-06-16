package me.steffenjacobs.iotplatformintegrator.domain.shared.item;

/** @author Steffen Jacobs */
public class SharedItem {

	private final String name;
	private final String label;
	private final ItemType type;

	public SharedItem(String name, String label, ItemType type) {
		super();
		this.name = name;
		this.label = label;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public ItemType getType() {
		return type;
	}

}
