package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;

/** @author Steffen Jacobs */
public class SharedAction implements SharedRuleElement{
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
