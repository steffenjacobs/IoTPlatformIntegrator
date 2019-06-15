package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.TriggerType;

/** @author Steffen Jacobs */
public class OpenHabRuleTransformationAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHabRuleTransformationAdapter.class);

	public SharedCondition transformCondition(Condition condition) {
		if (condition.getConfiguration() == null || condition.getConfiguration().getAdditionalProperties() == null) {
			LOG.info("Could not parse condition {}", condition.getId());
			return null;
		}
		final Map<String, Object> properties = condition.getConfiguration().getAdditionalProperties();
		String description = condition.getDescription();
		String type = condition.getType();
		String label = condition.getLabel();
		return new SharedCondition(getConditiontype(type), properties, description, label);
	}

	public SharedTrigger transformTrigger(Trigger t) {
		if (t.getConfiguration() == null || t.getConfiguration().getAdditionalProperties() == null || t.getConfiguration().getAdditionalProperties().isEmpty()) {
			LOG.info("Could not parse trigger {}", t.getId());
			return null;
		}
		final Map<String, Object> properties = t.getConfiguration().getAdditionalProperties();
		String description = t.getDescription();
		String type = t.getType();
		String label = t.getLabel();
		return new SharedTrigger(getTriggerType(type), properties, description, label);
	}

	private ConditionType getConditiontype(String conditionType) {
		switch (conditionType) {
		case "timer.DayOfWeekCondition":
			LOG.error("DayOfWeek is currently (openHab version 2.4.0) broken!");
			return ConditionType.DayOfWeek;
		case "script.ScriptCondition":
			return ConditionType.ScriptEvaluatesTrue;
		case "core.ItemStateCondition":
			return ConditionType.ItemState;
		case "core.TimeOfDayCondition":
			return ConditionType.TimeOfDay;
		}
		return ConditionType.Unknown;
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
			LOG.info("Could not parse action {}", a.getId());
			return null;
		}
		final Map<String, Object> properties = a.getConfiguration().getAdditionalProperties();
		String description = a.getDescription();
		String type = a.getType();
		String label = a.getLabel();
		return new SharedAction(getActionType(type), properties, description, label);
	}

	private ActionType getActionType(String actionType) {
		switch (actionType) {
		case "core.RuleEnablementAction":
			return ActionType.EnableDisableRule;
		case "script.ScriptAction":
			return ActionType.ExecuteScript;
		case "media.PlayAction":
			return ActionType.PlaySound;
		case "core.RunRuleAction":
			return ActionType.RunRules;
		case "media.SayAction":
			return ActionType.SaySomething;
		case "core.ItemCommandAction":
			return ActionType.ItemCommand;
		}
		return ActionType.Unknown;
	}
}
