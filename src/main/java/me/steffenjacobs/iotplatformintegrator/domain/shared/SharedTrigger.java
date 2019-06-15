package me.steffenjacobs.iotplatformintegrator.domain.shared;

/** @author Steffen Jacobs */
public class SharedTrigger {

	private final String description;
	private final String type;
	private final String label;
	private final String itemName;

	public SharedTrigger(String description, String type, String label, String itemName) {
		super();
		this.description = description;
		this.type = type;
		this.label = label;
		this.itemName = itemName;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getItemName() {
		return itemName;
	}

	public String getLabel() {
		return label;
	}

}
