package me.steffenjacobs.iotplatformintegrator.domain.shared;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.ConditionType.ConditionTypeSpecificKey;

/** @author Steffen Jacobs */
public class ConditionTypeContainer {
	private final ConditionType conditionType;

	private final Map<ConditionTypeSpecificKey, Object> conditionTypeSpecificValues;

	public ConditionTypeContainer(ConditionType conditionType, Map<String, Object> propertiesMap) {
		this.conditionType = conditionType;
		this.conditionTypeSpecificValues = new HashMap<>();
		for (ConditionTypeSpecificKey key : conditionType.getTypeSpecificKeys()) {
			conditionTypeSpecificValues.put(key, propertiesMap.get(key.getKeyString()));
		}
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public Map<ConditionTypeSpecificKey, Object> getConditionTypeSpecificValues() {
		return conditionTypeSpecificValues;
	}
}
