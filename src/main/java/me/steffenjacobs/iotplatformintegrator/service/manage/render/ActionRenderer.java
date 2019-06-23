package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;

/** @author Steffen Jacobs */
public class ActionRenderer<T> implements ItemPlaceholderFactory{

	private static final Logger LOG = LoggerFactory.getLogger(ActionRenderer.class);

	private final RenderingStrategy<T> renderingStrategy;

	public ActionRenderer(RenderingStrategy<T> renderingStrategy) {
		this.renderingStrategy = renderingStrategy;
	}

	public Collection<T> renderAction(SharedAction action) {
		switch (action.getActionTypeContainer().getActionType()) {
		case EnableDisableRule:
			boolean enable = (boolean) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Enable);
			String rules = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			List<T> tokens = new ArrayList<>();
			tokens.add(renderingStrategy.textComponent("Set", "Set rule.enabled for rules %s to %s"));
			tokens.add(renderingStrategy.textComponent("rule.enabled", "Set rule.enabled for rules %s to %s"));
			tokens.add(renderingStrategy.textComponent("for", "Set rule.enabled for rules %s to %s"));
			tokens.add(renderingStrategy.textComponent("rules", "Set rule.enabled for rules %s to %s"));
			tokens.addAll(renderingStrategy.valueComponent(rules, ActionTypeSpecificKey.RuleUUIDs));
			tokens.add(renderingStrategy.textComponent("to", "Set rule.enabled for rules %s to %s"));
			tokens.addAll(renderingStrategy.valueComponent(Boolean.toString(enable), ActionTypeSpecificKey.Enable));
			return tokens;
		case ExecuteScript:
			String type = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Type);
			String script = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Script);
			List<T> tokens2 = new ArrayList<>();
			tokens2.add(renderingStrategy.textComponent("Execute", "Execute script %s {%s}"));
			tokens2.add(renderingStrategy.textComponent("script", "Execute script %s {%s}"));
			tokens2.addAll(renderingStrategy.valueComponent(type, ActionTypeSpecificKey.Type));
			tokens2.add(renderingStrategy.textComponent("{", "Execute script %s {%s}"));
			tokens2.addAll(renderingStrategy.valueComponent(script, ActionTypeSpecificKey.Script));
			tokens2.add(renderingStrategy.textComponent("}", "Execute script %s {%s}"));
			return tokens2;
		case PlaySound:
			String sink = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String sound = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sound);
			List<T> tokens3 = new ArrayList<>();
			tokens3.add(renderingStrategy.textComponent("Play", "Play sound %s to %s"));
			tokens3.add(renderingStrategy.textComponent("sound", "Play sound %s to %s"));
			tokens3.addAll(renderingStrategy.valueComponent(sound, ActionTypeSpecificKey.Sound));
			tokens3.add(renderingStrategy.textComponent("to", "Play sound %s to %s"));
			tokens3.addAll(renderingStrategy.valueComponent(sink, ActionTypeSpecificKey.Sink));
			return tokens3;
		case RunRules:
			String rules2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.RuleUUIDs);
			boolean considerConditions = (boolean) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ConsiderConditions);
			List<T> tokens4 = new ArrayList<>();
			tokens4.add(renderingStrategy.textComponent("Run", "Run rules %s Check condition: %s"));
			tokens4.add(renderingStrategy.textComponent("rules", "Run rules %s Check condition: %s"));
			tokens4.addAll(renderingStrategy.valueComponent(rules2, ActionTypeSpecificKey.RuleUUIDs));
			tokens4.add(renderingStrategy.textComponent("Check", "Run rules %s Check condition: %s"));
			tokens4.add(renderingStrategy.textComponent("condition:", "Run rules %s Check condition: %s"));
			tokens4.addAll(renderingStrategy.valueComponent(Boolean.toString(considerConditions), ActionTypeSpecificKey.ConsiderConditions));
			return tokens4;
		case SaySomething:
			String sink2 = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink);
			String text = "" + action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Text);
			List<T> tokens5 = new ArrayList<>();
			tokens5.add(renderingStrategy.textComponent("Say", "Say %s to %s"));
			tokens5.addAll(renderingStrategy.valueComponent(text, ActionTypeSpecificKey.Text));
			tokens5.add(renderingStrategy.textComponent("to", "Say %s to %s"));
			tokens5.addAll(renderingStrategy.valueComponent(sink2, ActionTypeSpecificKey.Sink));
			return tokens5;
		case ItemCommand:
			SharedItem item = getItemOrPlaceholder(action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName));
			Command command = (Command) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Command);
			List<T> tokens6 = new ArrayList<>();
			tokens6.add(renderingStrategy.textComponent("Send", "Send command %s to %s"));
			tokens6.add(renderingStrategy.textComponent("command", "Send command %s to %s"));
			tokens6.add(renderingStrategy.commandComponent(command, ActionTypeSpecificKey.Command));
			tokens6.add(renderingStrategy.textComponent("to", "Send command %s to %s"));
			tokens6.add(renderingStrategy.itemComponent(item, ActionTypeSpecificKey.ItemName));
			return tokens6;
		default:
			LOG.error("Invalid action type: {}", action.getActionTypeContainer().getActionType());
			return Arrays.asList(renderingStrategy.textComponent("<An error occured during parsing of the action.>", "<An error occured during parsing of the action.>"));
		}
	}
}
