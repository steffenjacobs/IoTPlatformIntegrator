package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.TriggerType;

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

	public SharedTrigger transformTrigger(Trigger t) {
		if (t.getConfiguration() == null || t.getConfiguration().getAdditionalProperties() == null || t.getConfiguration().getAdditionalProperties().isEmpty()) {
			return null;
		}
		final Map<String, Object> properties = t.getConfiguration().getAdditionalProperties();
		String description = t.getDescription();
		String type = t.getType();
		String label = t.getLabel();
		return new SharedTrigger(getTriggerType(type), properties, description, label);
	}

	private TriggerType getTriggerType(String triggerType) {
		switch (triggerType) {
		case "core.ItemStateChangeTrigger":
			return TriggerType.ItemStateChanged;
		case "core.ItemCommandTrigger":
			return TriggerType.CommandReceived;
		case "core.ItemStateUpdateTrigger":
			return TriggerType.ItemStateUpdated;
		case "timer.TimeOfDayTrigger":
			return TriggerType.Timed;
		case "core.ChannelEventTrigger":
			return TriggerType.TriggerChannelFired;
		}
		return TriggerType.Unknown;
	}

	public SharedAction transformAction(Action a) {
		if (a.getConfiguration() == null || a.getConfiguration().getAdditionalProperties() == null || a.getConfiguration().getAdditionalProperties().isEmpty()) {
			return null;
		}
		final Map<String, Object> properties = a.getConfiguration().getAdditionalProperties();
		String description = a.getDescription();
		String type = a.getType();
		String label = a.getLabel();
		String itemName = "" + properties.get("itemName");
		String command = "" + properties.get("command");
		return new SharedAction(description, type, label, itemName, command);
	}
}
