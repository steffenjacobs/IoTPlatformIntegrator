package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;

/** @author Steffen Jacobs */
public class SharedCondition implements SharedRuleElement{
	private final String description;
	private final String label;
	private final ConditionTypeContainer conditionTypeContainer;
	private final int relativeElementId;

	public SharedCondition(ConditionType conditionType, Map<String, Object> propertiesMap, String description, String label, int relativeElementId) {
		super();
		this.description = description;
		this.label = label;
		this.relativeElementId = relativeElementId;

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

	@Override
	public int getRelativeElementId() {
		return relativeElementId;
	}
}
