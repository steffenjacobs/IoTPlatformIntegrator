package me.steffenjacobs.iotplatformintegrator.domain.shared;

import java.util.Map;

/** @author Steffen Jacobs */
public class SharedCondition {
	private final String description;
	private final String label;
	private final ConditionTypeContainer conditionTypeContainer;

	public SharedCondition(ConditionType conditionType, Map<String, Object> propertiesMap, String description, String label) {
		super();
		this.description = description;
		this.label = label;

		this.conditionTypeContainer = new ConditionTypeContainer(conditionType, propertiesMap);
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public ConditionTypeContainer getConditionTypeContainer() {
		return conditionTypeContainer;
	}
}
