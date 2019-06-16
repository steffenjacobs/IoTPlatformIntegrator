package me.steffenjacobs.iotplatformintegrator.domain.shared.item;

/** @author Steffen Jacobs */
public class SharedItem {

	private final String name;
	private final String label;
	private final String type;

	public SharedItem(String name, String label, String type) {
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

	public String getType() {
		return type;
	}

}
