package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;

/** @author Steffen Jacobs */
public class SharedCondition implements SharedRuleElement {
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

	/** Copy constructor. */
	public SharedCondition(SharedCondition c) {
		this.description = c.getDescription();
		this.label = c.getLabel();
		this.relativeElementId = c.getRelativeElementId();

		final Map<String, Object> propertiesMap = new HashMap<>();
		c.getConditionTypeContainer().getConditionTypeSpecificValues().forEach((k, v) -> propertiesMap.put(k.getKeyString(), v));

		this.conditionTypeContainer = new ConditionTypeContainer(c.getConditionTypeContainer().getConditionType(), propertiesMap);
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
