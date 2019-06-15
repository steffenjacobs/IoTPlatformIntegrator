package me.steffenjacobs.iotplatformintegrator.domain.shared;

/** @author Steffen Jacobs */
public class SharedAction {
	private final String description;
	private final String type;
	private final String label;
	private final String itemName;
	private final String command;

	public SharedAction(String description, String type, String label, String itemName, String command) {
		super();
		this.description = description;
		this.type = type;
		this.label = label;
		this.itemName = itemName;
		this.command = command;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public String getItemName() {
		return itemName;
	}

	public String getCommand() {
		return command;
	}

}
