package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformRuleTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabRuleTransformationAdapter implements PlatformRuleTransformationAdapter<ExperimentalRule> {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHabRuleTransformationAdapter.class);

	private final OpenHabTriggerTransformationAdapter triggerTransformer;
	private final OpenHabConditionTransformationAdapter conditionTransformer;
	private final OpenHabActionTransformationAdapter actionTransformer;

	public OpenHabRuleTransformationAdapter() {
		triggerTransformer = new OpenHabTriggerTransformationAdapter();
		conditionTransformer = new OpenHabConditionTransformationAdapter();
		actionTransformer = new OpenHabActionTransformationAdapter();
	}

	@Override
	public SharedRule transformRule(ExperimentalRule rule, ItemDirectory itemDirectory, OpenHabCommandParser commandParser) {
		String ruleName = rule.getName();
		String ruleUid = rule.getUid();
		String description = rule.getDescription();
		String status = getReadeableStatus(rule);
		String visible = rule.getVisibility();

		Set<SharedCondition> conditions = new HashSet<>();
		int conditionCount = 0;
		for (Condition condition : rule.getConditions()) {
			conditions.add(transformCondition(condition, itemDirectory, conditionCount++));
		}
		Set<SharedTrigger> triggers = new HashSet<>();
		int triggerCount = 0;
		for (Trigger trigger : rule.getTriggers()) {
			triggers.add(transformTrigger(trigger, itemDirectory, commandParser, triggerCount++));
		}
		Set<SharedAction> actions = new HashSet<>();
		int actionCount = 0;
		for (Action action : rule.getActions()) {
			actions.add(transformAction(action, itemDirectory, commandParser, actionCount++));
		}
		LOG.info("Transformed rule {}.", rule.getUid());
		return new SharedRule(ruleName, ruleUid, description, visible, status, triggers, conditions, actions);
	}

	private SharedCondition transformCondition(Condition condition, ItemDirectory itemDirectory, int relativeElementId) {
		if (condition.getConfiguration() == null || condition.getConfiguration().getAdditionalProperties() == null) {
			LOG.info("Could not parse condition {}", condition.getId());
			return null;
		}
		final Map<String, Object> properties = condition.getConfiguration().getAdditionalProperties();
		String description = condition.getDescription();
		String type = condition.getType();
		String label = condition.getLabel();
		SharedCondition sc = new SharedCondition(getConditionType(type), properties, description, label, relativeElementId);
		conditionTransformer.convertConditionTypeContainer(sc.getConditionTypeContainer(), itemDirectory);
		return sc;
	}

	private SharedTrigger transformTrigger(Trigger t, ItemDirectory itemDirectory, OpenHabCommandParser commandParser, int relativeElementId) {
		if (t.getConfiguration() == null || t.getConfiguration().getAdditionalProperties() == null || t.getConfiguration().getAdditionalProperties().isEmpty()) {
			LOG.info("Could not parse trigger {}", t.getId());
			return null;
		}
		final Map<String, Object> properties = new HashMap<>();
		properties.putAll(t.getConfiguration().getAdditionalProperties());
		String description = t.getDescription();
		String type = t.getType();
		String label = t.getLabel();

		SharedTrigger st = new SharedTrigger(getTriggerType(type), properties, description, label, relativeElementId);
		triggerTransformer.convertTriggerTypeContainer(st.getTriggerTypeContainer(), itemDirectory, commandParser);
		return st;
	}

	private ConditionType getConditionType(String conditionType) {
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

	private SharedAction transformAction(Action a, ItemDirectory itemDirectory, OpenHabCommandParser commandParser, int relativeElementId) {
		if (a.getConfiguration() == null || a.getConfiguration().getAdditionalProperties() == null || a.getConfiguration().getAdditionalProperties().isEmpty()) {
			LOG.info("Could not parse action {}", a.getId());
			return null;
		}
		final Map<String, Object> properties = a.getConfiguration().getAdditionalProperties();
		String description = a.getDescription();
		String type = a.getType();
		String label = a.getLabel();
		SharedAction sa = new SharedAction(getActionType(type), properties, description, label, relativeElementId);
		actionTransformer.convertActionTypeContainer(sa.getActionTypeContainer(), itemDirectory, commandParser);
		return sa;
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

	private String getReadeableStatus(ExperimentalRule rule) {
		if (rule.getStatus() == null) {
			return "-";
		}
		return rule.getStatus().getStatus();
	}
}
