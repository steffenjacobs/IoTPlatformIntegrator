package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.ReferenceToken;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public class PseudocodeGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(PseudocodeGenerator.class);

	public List<Token> generateCodeForRule(SharedRule sharedRule) {
		if (sharedRule == null) {
			return Arrays.asList(new Token("Please select a rule to generate pseudocode for.", Token.TokenType.UNCLASSIFIED, ""));
		}

		List<Token> tokens = new ArrayList<>();

		// triggers
		addToken(tokens, keywordToken("WHEN"));
		List<String> triggers = new ArrayList<>();
		for (SharedTrigger trigger : sharedRule.getTriggers()) {
			triggers.add(generateCodeForTrigger(trigger));
		}
		addToken(tokens, unclassifiedToken("\n    "));
		for (int i = 0; i < triggers.size(); i++) {
			addToken(tokens, new Token(triggers.get(i), TokenType.UNKNOWN, triggers.get(i)));
			if (i < triggers.size() - 1) {
				addToken(tokens, unclassifiedToken("\n    "));
				addToken(tokens, operatorToken("\u2228"));
			}
		}
		addToken(tokens, unclassifiedToken("\n"));

		// conditions
		if (!sharedRule.getConditions().isEmpty()) {

			addToken(tokens, keywordToken("\nIF"));
			List<String> conditions = new ArrayList<>();
			for (SharedCondition condition : sharedRule.getConditions()) {
				conditions.add(generateCodeForCondition(condition));
			}
			addToken(tokens, unclassifiedToken("\n    "));
			for (int i = 0; i < conditions.size(); i++) {
				addToken(tokens, new Token(conditions.get(i), TokenType.UNKNOWN, conditions.get(i)));
				if (i < triggers.size() - 1) {
					addToken(tokens, unclassifiedToken("\n    "));
					addToken(tokens, operatorToken("\u2228"));
				}
			}
			addToken(tokens, unclassifiedToken("\n"));
		}

		// actions
		if (!sharedRule.getActions().isEmpty()) {

			addToken(tokens, keywordToken("\nDO"));
			List<String> actions = new ArrayList<>();
			for (SharedAction action : sharedRule.getActions()) {
				actions.add(generateCodeForAction(action));
			}
			addToken(tokens, unclassifiedToken("\n    "));
			for (int i = 0; i < actions.size(); i++) {
				addToken(tokens, new Token(actions.get(i), TokenType.UNKNOWN, actions.get(i)));
				if (i < triggers.size() - 1) {
					addToken(tokens, unclassifiedToken("\n    "));
				}
			}
			addToken(tokens, unclassifiedToken("\n"));
		}
		LOG.info("Generated {} tokens.", tokens.size());
		return tokens;
	}

	public void addToken(List<Token> tokens, Token t) {
		tokens.add(t);
		int cnt = StringUtils.countMatches(t.getText().replaceAll(" +", " "), " ");
		if (cnt > 1) {
			ReferenceToken rt = new ReferenceToken(t);
			for (int i = 0; i < cnt; i++) {
				tokens.add(rt);
			}
		}
	}

	private Token keywordToken(String keyword) {
		return new Token(keyword, TokenType.KEYWORD, "Keyword " + keyword);
	}

	private Token unclassifiedToken(String text) {
		return new Token(text, TokenType.UNCLASSIFIED, "");
	}

	private Token operatorToken(String operator) {
		return new Token(operator, TokenType.OPERATOR, "Operator " + operator);
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
