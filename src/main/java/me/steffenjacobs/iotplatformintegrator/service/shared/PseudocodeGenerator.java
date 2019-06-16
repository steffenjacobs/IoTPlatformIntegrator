package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class PseudocodeGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(PseudocodeGenerator.class);

	public String generateCodeForRule(SharedRule sharedRule) {
		if (sharedRule == null) {
			return "Please select a rule to generate pseudocode for.";
		}
		StringBuilder sb = new StringBuilder();

		// triggers
		sb.append("WHEN");
		List<String> triggers = new ArrayList<>();
		for (SharedTrigger trigger : sharedRule.getTriggers()) {
			triggers.add(generateCodeForTrigger(trigger));
		}
		sb.append("\n    ");
		sb.append(String.join("\n    \u2228 ", triggers));
		sb.append("\n\n");

		// conditions
		if (!sharedRule.getConditions().isEmpty()) {

			sb.append("IF");
			List<String> conditions = new ArrayList<>();
			for (SharedCondition condition : sharedRule.getConditions()) {
				conditions.add(generateCodeForCondition(condition));
			}
			sb.append("\n    ");
			sb.append(String.join("\n    \u2228 ", conditions));
			sb.append("\n\n");
		}

		// actions
		if (!sharedRule.getActions().isEmpty()) {

			sb.append("DO");
			List<String> actions = new ArrayList<>();
			for (SharedAction action : sharedRule.getActions()) {
				actions.add(generateCodeForAction(action));
			}
			sb.append("\n    ");
			sb.append(String.join("\n    ", actions));
			sb.append("\n\n");
		}

		return sb.toString();
	}

	public String generateCodeForTrigger(SharedTrigger trigger) {

		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			String itemName = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				return String.format("Item '%s' changed from %s to %s", itemName, previousState, state);
			}
			return String.format("Item '%s' changed to %s", itemName, state);
		case CommandReceived:
			String command = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			String itemName2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			return String.format("Item '%s' received command '%s'", itemName2, command);
		case ItemStateUpdated:
			String itemName3 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			return String.format("Item '%s' was updated to %s", itemName3, state2);
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			return String.format("Time is equal to %s", time);
		case TriggerChannelFired:
			String triggerChannel = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			return String.format("Channel '%s' received event '%s'", triggerChannel, event);
		default:
			LOG.error("Invalid trigger type: {}", trigger.getTriggerTypeContainer().getTriggerType());
			return "<An error occured during parsing of the trigger.>";
		}
	}

	public String generateCodeForCondition(SharedCondition condition) {
		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ScriptEvaluatesTrue:
			String script = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Script);
			String type = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Type);
			return String.format("Script type=%s {%s} evaluated true", type, script);
		case ItemState:
			String itemName = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName);
			String state = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.State);
			String operator = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Operator);
			return String.format("value of item '%s' %s %s", itemName, operator, state);
		case DayOfWeek:
			return "<Day of Week is not implemented with openHAB 2.4.0>";
		case TimeOfDay:
			String startTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.StartTime);
			String endTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.EndTime);
			return String.format("time between %s and %s", startTime, endTime);
		default:
			LOG.error("Invalid condition type: {}", condition.getConditionTypeContainer().getConditionType());
			return "<An error occured during parsing of the condition.>";
		}
	}

	public String generateCodeForAction(SharedAction action) {
		switch (action.getActionTypeContainer().getActionType()) {
		case EnableDisableRule:
			String enable = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Enable);
			String rules = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			boolean e = Boolean.parseBoolean(enable);
			return String.format((e ? "Enable" : "Disable") + " rules %s", rules);
		case ExecuteScript:
			String type = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Type);
			String script = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Script);
			return String.format("Execute script '%s' (%s)", script, type);
		case PlaySound:
			String sink = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String sound = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sound);
			return String.format("Play sound '%s' to '%s'", sound, sink);
		case RunRules:
			String rules2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			String considerConditions = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ConsiderConditions);
			boolean cc = Boolean.valueOf(considerConditions);
			return String.format("Run %s with" + (cc ? "" : "out") + " checking associated conditions", rules2);
		case SaySomething:
			String sink2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String text = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Text);
			return String.format("Say '%s' to '%s'", text, sink2);
		case ItemCommand:
			String itemName = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName);
			String command = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Command);
			return String.format("Send command %s to item '%s'", command, itemName);
		default:
			LOG.error("Invalid action type: {}", action.getActionTypeContainer().getActionType());
			return "<An error occured during parsing of the action.>";
		}

	}

}
