package me.steffenjacobs.iotplatformintegrator.domain.shared;

import java.util.Map;

/** @author Steffen Jacobs */
public class SharedAction {
	private final String description;
	private final String label;

	private final ActionTypeContainer actionTypeContainer;

	public SharedAction(ActionType actionType, Map<String, Object> properties, String description, String label) {
		super();
		this.description = description;
		this.label = label;

		this.actionTypeContainer = new ActionTypeContainer(actionType, properties);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public ActionTypeContainer getActionTypeContainer() {
		return actionTypeContainer;
	}
}
