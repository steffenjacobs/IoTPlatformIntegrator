package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;

/** @author Steffen Jacobs */
public class SharedTrigger implements SharedRuleElement {

	private final String description;
	private final String label;
	private final TriggerTypeContainer triggerTypeContainer;
	private final int relativeElementId;

	public SharedTrigger(TriggerType triggerType, Map<String, Object> properties, String description, String label,
			int relativeElementId) {
		super();
		this.description = description;
		this.label = label;
		this.relativeElementId = relativeElementId;

		this.triggerTypeContainer = new TriggerTypeContainer(triggerType, properties);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public TriggerTypeContainer getTriggerTypeContainer() {
		return triggerTypeContainer;
	}

	@Override
	public int getRelativeElementId() {
		return relativeElementId;
	}

}
