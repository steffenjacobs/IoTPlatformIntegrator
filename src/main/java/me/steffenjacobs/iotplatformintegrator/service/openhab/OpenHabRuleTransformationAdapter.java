package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedCondition;

/** @author Steffen Jacobs */
public class OpenHabRuleTransformationAdapter {

	public SharedCondition transformCondition(Condition condition) {
		if (condition.getConfiguration() == null || condition.getConfiguration().getAdditionalProperties() == null
				|| condition.getConfiguration().getAdditionalProperties().isEmpty()) {
			return null;
		}
		final Map<String, Object> properties = condition.getConfiguration().getAdditionalProperties();
		String description = condition.getDescription();
		String type = condition.getType();
		String label = condition.getLabel();
		String itemName = "" + properties.get("itemName");
		String operator = "" + properties.get("operator");
		String state = "" + properties.get("state");
		return new SharedCondition(description, type, label, itemName, operator, state);
	}
}
