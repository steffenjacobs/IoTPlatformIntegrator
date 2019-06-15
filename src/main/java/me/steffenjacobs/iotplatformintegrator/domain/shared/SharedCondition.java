package me.steffenjacobs.iotplatformintegrator.domain.shared;

/** @author Steffen Jacobs */
public class SharedCondition {
	private final String description;
	private final String itemName;
	private final String operator;
	private final String state;
	private final String type;
	private final String label;

	public SharedCondition(String description, String type, String label, String itemName, String operator, String state) {
		super();
		this.description = description;
		this.type = type;
		this.label = label;
		this.itemName = itemName;
		this.operator = operator;
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public String getItemName() {
		return itemName;
	}

	public String getOperator() {
		return operator;
	}

	public String getState() {
		return state;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

}
