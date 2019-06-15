package me.steffenjacobs.iotplatformintegrator.domain.shared;

import java.util.HashMap;
import java.util.Map;
import me.steffenjacobs.iotplatformintegrator.domain.shared.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class TriggerTypeContainer {

	private final TriggerType triggerType;

	private final Map<TriggerTypeSpecificKey, Object> triggerTypeSpecificValues;

	public TriggerTypeContainer(TriggerType triggerType, Map<String, Object> propertiesMap) {
		this.triggerType = triggerType;
		this.triggerTypeSpecificValues = new HashMap<>();
		for (TriggerTypeSpecificKey key : triggerType.getTypeSpecificKeys()) {
			triggerTypeSpecificValues.put(key, propertiesMap.get(key.getKeyString()));
		}
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}

	public Map<TriggerTypeSpecificKey, Object> getTriggerTypeSpecificValues() {
		return triggerTypeSpecificValues;
	}

}
