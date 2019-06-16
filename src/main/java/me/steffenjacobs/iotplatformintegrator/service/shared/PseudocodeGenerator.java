package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
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
		int count = 0;
		for (SharedTrigger trigger : sharedRule.getTriggers()) {
			for (Token t : generateCodeForTrigger(trigger)) {
				addToken(tokens, t);
			}
			if (count < sharedRule.getTriggers().size() - 1) {
				addToken(tokens, operatorToken(Operation.OR));
			}
			count++;
		}

		// conditions
		if (!sharedRule.getConditions().isEmpty()) {

			addToken(tokens, keywordToken("IF"));
			count = 0;
			for (SharedCondition condition : sharedRule.getConditions()) {
				for (Token t : generateCodeForCondition(condition)) {
					addToken(tokens, t);
				}
				if (count < sharedRule.getConditions().size() - 1) {
					addToken(tokens, operatorToken(Operation.OR));
				}
				count++;
			}
		}

		// actions
		if (!sharedRule.getActions().isEmpty()) {

			addToken(tokens, keywordToken("DO"));
			count = 0;
			for (SharedAction action : sharedRule.getActions()) {
				for (Token t : generateCodeForAction(action)) {
					addToken(tokens, t);
				}
				if (count < sharedRule.getActions().size() - 1) {
					addToken(tokens, operatorToken(Operation.AND));
				}
				count++;
			}
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

	private Token operatorToken(Operation operation) {
		return new Token(operation.getText(), TokenType.OPERATOR, String.format("Type: %s, Operator: %s (%s)", TokenType.OPERATOR, operation.getText(), operation.name()));
	}

	private Token triggerToken(String text, String description) {
		return new Token(text, TokenType.TRIGGER_CONDITION, description);
	}

	private Token conditionToken(String text, String description) {
		return new Token(text, TokenType.CONDITION, description);
	}

	private Token actionToken(String text, String description) {
		return new Token(text, TokenType.ACTION, description);
	}

	private Token itemToken(SharedItem item) {
		return new Token(item.getName(), TokenType.ITEM, String.format("Type: %s, Name: %s (%s)", TokenType.ITEM, item.getName(), item.getLabel()));
	}

	private List<Token> valueToken(String value) {
		if (value.contains(" ")) {
			return multivalueToken(value);
		}
		return Arrays.asList(new Token(value, TokenType.VALUE, String.format("Type: %s, Value: %s", TokenType.VALUE, value)));
	}

	private List<Token> multivalueToken(String value) {
		String[] values = value.trim().split(" ");
		List<Token> tokens = new ArrayList<>();
		for (String val : values) {
			tokens.add(new Token(val, TokenType.MULTI_VALUE, String.format("Type: %s, Value: %s", TokenType.MULTI_VALUE, value.trim())));
		}
		return tokens;
	}

	private Token commandToken(Command command) {
		return new Token(command.name(), TokenType.COMMAND, String.format("Type: %s, Command: %s", TokenType.COMMAND, command.name()));
	}

	private Token unknownToken(String message) {
		return new Token(message, TokenType.UNKNOWN, message);
	}

	public List<Token> generateCodeForTrigger(SharedTrigger trigger) {

		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			SharedItem item = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				List<Token> tokens = new ArrayList<>();
				tokens.add(triggerToken("Item", "Item '%s' changed from %s to %s"));
				tokens.add(itemToken(item));
				tokens.add(triggerToken("changed", "Item '%s' changed from %s to %s"));
				tokens.add(triggerToken("from", "Item '%s' changed from %s to %s"));
				tokens.addAll(valueToken(previousState));
				tokens.add(triggerToken("to", "Item '%s' changed from %s to %s"));
				tokens.addAll(valueToken(state));
				return tokens;
			}
			List<Token> tokens = new ArrayList<>();
			tokens.add(triggerToken("Item", "Item '%s' changed to %s"));
			tokens.add(itemToken(item));
			tokens.add(triggerToken("changed", "Item '%s' changed to %s"));
			tokens.addAll(valueToken(state));
			return tokens;
		case CommandReceived:
			Command command = (Command) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			SharedItem item2 = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			List<Token> tokens2 = new ArrayList<>();
			tokens2.add(triggerToken("Item", "Item '%s' received command '%s'"));
			tokens2.add(itemToken(item2));
			tokens2.add(triggerToken("received", "Item '%s' received command '%s'"));
			tokens2.add(triggerToken("command", "Item '%s' received command '%s'"));
			tokens2.add(commandToken(command));
			return tokens2;
		case ItemStateUpdated:
			SharedItem item3 = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			List<Token> tokens3 = new ArrayList<>();
			tokens3.add(triggerToken("Item", "Item '%s' was updated to %s"));
			tokens3.add(itemToken(item3));
			tokens3.add(triggerToken("was", "Item '%s' was updated to %s"));
			tokens3.add(triggerToken("updated", "Item '%s' was updated to %s"));
			tokens3.add(triggerToken("to", "Item '%s' was updated to %s"));
			tokens3.addAll(valueToken(state2));
			return tokens3;
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			List<Token> tokens4 = new ArrayList<>();
			tokens4.add(triggerToken("Time", "Time == %s"));
			tokens4.add(operatorToken(Operation.EQUAL));
			tokens4.addAll(valueToken(time));
			return tokens4;
		case TriggerChannelFired:
			String triggerChannel = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			List<Token> tokens5 = new ArrayList<>();
			tokens5.add(triggerToken("Channel", "Channel '%s' received event '%s'"));
			tokens5.addAll(valueToken(triggerChannel));
			tokens5.add(triggerToken("received", "Channel '%s' received event '%s'"));
			tokens5.add(triggerToken("event", "Channel '%s' received event '%s'"));
			tokens5.addAll(valueToken(event));
			return tokens5;
		default:
			LOG.error("Invalid trigger type: {}", trigger.getTriggerTypeContainer().getTriggerType());
			return Arrays.asList(unknownToken("<An error occured during parsing of the trigger.>"));
		}
	}

	public List<Token> generateCodeForCondition(SharedCondition condition) {
		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ScriptEvaluatesTrue:
			List<Token> tokens = new ArrayList<>();
			String script = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Script);
			String type = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Type);
			tokens.add(conditionToken("Script", "Script %s {%s} evaluates to true"));
			tokens.addAll(valueToken(type));
			tokens.add(unknownToken("{"));
			tokens.addAll(valueToken(script));
			tokens.add(unknownToken("}"));
			tokens.add(conditionToken("evaluates", "Script %s {%s} evaluates to true"));
			tokens.add(conditionToken("to", "Script %s {%s} evaluates to true"));
			tokens.add(conditionToken("true", "Script %s {%s} evaluates to true"));
			return tokens;
		case ItemState:
			SharedItem item = (SharedItem) condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName);
			String state = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.State);
			Operation operator = (Operation) condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Operator);
			List<Token> tokens2 = new ArrayList<>();
			tokens2.add(conditionToken("value", "value of item '%s' %s %s"));
			tokens2.add(conditionToken("of", "value of item '%s' %s %s"));
			tokens2.add(conditionToken("item", "value of item '%s' %s %s"));
			tokens2.add(itemToken(item));
			tokens2.add(operatorToken(operator));
			tokens2.addAll(valueToken(state));
			return tokens2;
		case DayOfWeek:
			return Arrays.asList(unknownToken("<Day of Week is not implemented with openHAB 2.4.0>"));
		case TimeOfDay:
			String startTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.StartTime);
			String endTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.EndTime);
			List<Token> tokens3 = new ArrayList<>();
			tokens3.add(conditionToken("time", "time between %s and %s"));
			tokens3.add(conditionToken("between", "time between %s and %s"));
			tokens3.addAll(valueToken(startTime));
			tokens3.add(conditionToken("and", "time between %s and %s"));
			tokens3.addAll(valueToken(endTime));
			return tokens3;
		default:
			LOG.error("Invalid condition type: {}", condition.getConditionTypeContainer().getConditionType());
			return Arrays.asList(unknownToken("<An error occured during parsing of the condition.>"));
		}
	}

	public List<Token> generateCodeForAction(SharedAction action) {
		switch (action.getActionTypeContainer().getActionType()) {
		case EnableDisableRule:
			boolean enable = (boolean) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Enable);
			String rules = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			List<Token> tokens = new ArrayList<>();
			tokens.add(actionToken("Set", "Set rule.enabled for rules %s to %s"));
			tokens.add(actionToken("rule.enabled", "Set rule.enabled for rules %s to %s"));
			tokens.add(actionToken("for", "Set rule.enabled for rules %s to %s"));
			tokens.add(actionToken("rules", "Set rule.enabled for rules %s to %s"));
			tokens.addAll(valueToken(rules));
			tokens.add(actionToken("to", "Set rule.enabled for rules %s to %s"));
			tokens.addAll(valueToken(Boolean.toString(enable)));
			return tokens;
		case ExecuteScript:
			String type = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Type);
			String script = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Script);
			List<Token> tokens2 = new ArrayList<>();
			tokens2.add(actionToken("Execute", "Execute script %s {%s}"));
			tokens2.add(actionToken("script", "Execute script %s {%s}"));
			tokens2.addAll(valueToken(type));
			tokens2.add(unknownToken("{"));
			tokens2.addAll(valueToken(script));
			tokens2.add(unknownToken("}"));
			return tokens2;
		case PlaySound:
			String sink = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String sound = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sound);
			List<Token> tokens3 = new ArrayList<>();
			tokens3.add(actionToken("Play", "Play sound %s to %s"));
			tokens3.add(actionToken("sound", "Play sound %s to %s"));
			tokens3.addAll(valueToken(sound));
			tokens3.add(actionToken("to", "Play sound %s to %s"));
			tokens3.addAll(valueToken(sink));
			return tokens3;
		case RunRules:
			String rules2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			boolean considerConditions = (boolean) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ConsiderConditions);
			List<Token> tokens4 = new ArrayList<>();
			tokens4.add(actionToken("Run", "Run rules %s Check condition: %s"));
			tokens4.add(actionToken("rules", "Run rules %s Check condition: %s"));
			tokens4.addAll(valueToken(rules2));
			tokens4.add(actionToken("Check", "Run rules %s Check condition: %s"));
			tokens4.add(actionToken("condition:", "Run rules %s Check condition: %s"));
			tokens4.addAll(valueToken(Boolean.toString(considerConditions)));
			return tokens4;
		case SaySomething:
			String sink2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String text = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Text);
			List<Token> tokens5 = new ArrayList<>();
			tokens5.add(actionToken("Say", "Say %s to %s"));
			tokens5.addAll(valueToken(text));
			tokens5.add(actionToken("to", "Say %s to %s"));
			tokens5.addAll(valueToken(sink2));
			return tokens5;
		case ItemCommand:
			SharedItem item = (SharedItem) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName);
			Command command = (Command) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Command);
			List<Token> tokens6 = new ArrayList<>();
			tokens6.add(actionToken("Send", "Send command %s to %s"));
			tokens6.add(actionToken("command", "Send command %s to %s"));
			tokens6.add(commandToken(command));
			tokens6.add(actionToken("to", "Send command %s to %s"));
			tokens6.add(itemToken(item));
			return tokens6;
		default:
			LOG.error("Invalid action type: {}", action.getActionTypeContainer().getActionType());
			return Arrays.asList(unknownToken("<An error occured during parsing of the action.>"));
		}

	}

}
