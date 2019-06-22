package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ActionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.TriggerElement;

/** @author Steffen Jacobs */
public class RuleRenderController {
	private static final Logger LOG = LoggerFactory.getLogger(RuleRenderController.class);

	private final RuleBuilder ruleBuilder;

	public RuleRenderController(RuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;

		EventBus.getInstance().addEventHandler(EventType.SelectedSourceRuleChanged, e -> renderRule(((SelectedSourceRuleChangeEvent) e).getSelectedRule()));
	}

	public void renderRule(SharedRule rule) {
		ruleBuilder.clear();
		if (rule == null) {
			return;
		}
		ruleBuilder.setHeader(rule.getName(), rule.getStatus(), rule.getDescription());

		for (SharedTrigger trigger : rule.getTriggers()) {
			ruleBuilder.appendDynamicElement(renderTrigger(trigger));
		}
		for (SharedCondition condition : rule.getConditions()) {
			ruleBuilder.appendDynamicElement(renderCondition(condition));
		}
		for (SharedAction action : rule.getActions()) {
			ruleBuilder.appendDynamicElement(renderAction(action));
		}
	}

	private DynamicElement renderTrigger(SharedTrigger trigger) {
		TriggerElement elem = new TriggerElement(ruleBuilder);
		elem.setTriggerTypeContainer(trigger.getTriggerTypeContainer());

		String label = trigger.getLabel();
		String description = trigger.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		elem.setStrategyElements(setupStrategy(trigger));
		return elem;
	}

	private Collection<Component> setupStrategy(SharedTrigger trigger) {
		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			Object item = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<Component> strategyElements = new ArrayList<>();

			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				strategyElements.add(textComponent("Item", "Item '%s' changed from %s to %s"));
				strategyElements.add(itemComponent(getItemOrPlaceholder(item)));
				strategyElements.add(textComponent("changed", "Item '%s' changed from %s to %s"));
				strategyElements.add(valueComponent(previousState));
				strategyElements.add(textComponent("from", "Item '%s' changed from %s to %s"));
				strategyElements.add(valueComponent(state));
				strategyElements.add(textComponent("to", "Item '%s' changed from %s to %s"));
				return strategyElements;
			}
			strategyElements.add(textComponent("Item", "Item '%s' changed to %s"));
			strategyElements.add(itemComponent(getItemOrPlaceholder(item)));
			strategyElements.add(textComponent("changed", "Item '%s' changed to %s"));
			strategyElements.add(textComponent("to", "Item '%s' changed to %s"));
			strategyElements.add(valueComponent(state));
			return strategyElements;
		case CommandReceived:
			Command command = (Command) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			SharedItem item2 = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			Collection<Component> strategyElements2 = new ArrayList<>();
			strategyElements2.add(textComponent("Item", "Item '%s' received command '%s'"));
			strategyElements2.add(itemComponent(getItemOrPlaceholder(item2)));
			strategyElements2.add(textComponent("received", "Item '%s' received command '%s'"));
			strategyElements2.add(textComponent("command", "Item '%s' received command '%s'"));
			strategyElements2.add(commandComponent(command));
			return strategyElements2;
		case ItemStateUpdated:
			Object item3 = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<Component> strategyElements3 = new ArrayList<>();

			strategyElements3.add(textComponent("Item", "Item '%s' updated to %s"));
			strategyElements3.add(itemComponent(getItemOrPlaceholder(item3)));
			strategyElements3.add(textComponent("updated", "Item '%s' updated to %s"));
			strategyElements3.add(textComponent("to", "Item '%s' updated to %s"));
			strategyElements3.add(valueComponent(state2));
			return strategyElements3;
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			Collection<Component> strategyElements4 = new ArrayList<>();

			strategyElements4.add(textComponent("Time", "Time == %s"));
			strategyElements4.add(operationComponent(Operation.EQUAL));
			strategyElements4.add(valueComponent(time));
			return strategyElements4;
		case TriggerChannelFired:
			Object triggerChannel = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			String eventData = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.EventData);

			Collection<Component> strategyElements5 = new ArrayList<>();

			strategyElements5.add(textComponent("Channel", "Channel '%s' received event '%s'"));
			if (triggerChannel instanceof String) {
				strategyElements5.add(valueComponent("" + triggerChannel));
			}
			if (triggerChannel instanceof SharedItem) {
				strategyElements5.add(itemComponent((SharedItem) triggerChannel));
			}
			strategyElements5.add(textComponent("received", "Channel '%s' received event '%s'"));
			strategyElements5.add(textComponent("event", "Channel '%s' received event '%s'"));
			strategyElements5.add(valueComponent(event));
			if (eventData != null && !eventData.isEmpty() && !"null".equals(eventData)) {
				strategyElements5.add(textComponent("{", "{'%s'}"));
				strategyElements5.add(valueComponent(eventData));
				strategyElements5.add(textComponent("}", "{'%s'}"));
			}
			return strategyElements5;
		default:
			LOG.error("Unsupported trigger type {}.", trigger.getTriggerTypeContainer().getTriggerType());
			return new ArrayList<Component>();
		}
	}

	private SharedItem getItemOrPlaceholder(Object item) {
		if (item instanceof SharedItem) {
			return (SharedItem) item;
		} else if (item instanceof String) {
			return new SharedItem("<invalid item name '" + item + "'>", "<invalid item name '" + item + "'>", ItemType.Unknown);
		} else {
			return new SharedItem("<null item>", "<null item>", ItemType.Unknown);
		}
	}

	private Component operationComponent(Operation operation) {
		DefaultComboBoxModel<Operation> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Operation> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Operation op : Operation.getKnownSubstitutes(operation)) {
			itemModel.addElement(op);
			// TODO: add alternative operations allowed in this item context
		}
		chooseItem.setSelectedItem(operation);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.OPERATOR, operation.name(), operation.name()));
		return chooseItem;
	}

	private Component commandComponent(Command command) {
		DefaultComboBoxModel<Command> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Command> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Command cmd : Command.getKnownSubstitutes(command)) {
			itemModel.addElement(cmd);
			// TODO: add alternative commands allowed by item
		}
		chooseItem.setSelectedItem(command);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.COMMAND, command.name(), command.name()));
		return chooseItem;
	}

	private Component valueComponent(String value) {
		// TODO: datatypes
		JTextField txt = new JTextField();
		txt.setText(value);
		return txt;
	}

	private Component textComponent(String text, String description) {
		JLabel label = new JLabel(text);
		label.setToolTipText(description);
		return label;
	}

	private Component itemComponent(SharedItem item) {
		DefaultComboBoxModel<SharedItem> itemModel = new DefaultComboBoxModel<>();
		JComboBox<SharedItem> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.getName() : ""));
		itemModel.addElement(item);
		chooseItem.setSelectedItem(item);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.ITEM, item.getName(), item.getLabel()));
		return chooseItem;
	}

	private DynamicElement renderAction(SharedAction action) {
		ActionElement elem = new ActionElement(ruleBuilder);
		elem.setActionTypeContainer(action.getActionTypeContainer());

		String label = action.getLabel();
		String description = action.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		switch (action.getActionTypeContainer().getActionType()) {
		default:
			LOG.error("Unsupported Action type {}.", action.getActionTypeContainer().getActionType());
		}
		return elem;
	}

	private ConditionElement renderCondition(SharedCondition condition) {
		ConditionElement elem = new ConditionElement(ruleBuilder);
		elem.setConditionTypeContainer(condition.getConditionTypeContainer());

		String label = condition.getLabel();
		String description = condition.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ItemState:
			break;
		default:
			LOG.error("Unsupported condition type {}.", condition.getConditionTypeContainer().getConditionType());
		}
		return elem;
	}

}
