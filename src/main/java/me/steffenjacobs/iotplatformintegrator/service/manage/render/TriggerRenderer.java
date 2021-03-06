package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.StateType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class TriggerRenderer<T> implements ItemPlaceholderFactory {

	private static final Logger LOG = LoggerFactory.getLogger(TriggerRenderer.class);

	private final RenderingStrategy<T> renderingStrategy;

	public TriggerRenderer(RenderingStrategy<T> renderingStrategy) {
		this.renderingStrategy = renderingStrategy;

	}

	public Collection<T> renderTrigger(SharedTrigger trigger) {
		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			Object item = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			SharedItem itemOrPlaceholder = getItemOrPlaceholder(item);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<T> strategyElements = new ArrayList<>();

			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				strategyElements.add(renderingStrategy.textComponent("Item", "Item '%s' changed from %s to %s"));
				strategyElements.add(renderingStrategy.itemComponent(itemOrPlaceholder, TriggerTypeSpecificKey.ItemName));
				strategyElements.add(renderingStrategy.textComponent("changed", "Item '%s' changed from %s to %s"));
				strategyElements.add(renderingStrategy.textComponent("from", "Item '%s' changed from %s to %s"));
				strategyElements.addAll(renderingStrategy.valueComponent(previousState, TriggerTypeSpecificKey.PreviousState));
				strategyElements.add(renderingStrategy.textComponent("to", "Item '%s' changed from %s to %s"));
				if (itemOrPlaceholder.getType().getAllowedStates().length == 1 && itemOrPlaceholder.getType().getAllowedStates()[0] == StateType.Command) {
					strategyElements.add(renderingStrategy.commandComponent(Command.parse(state), TriggerTypeSpecificKey.State));
				} else {
					strategyElements.addAll(renderingStrategy.valueComponent(state, TriggerTypeSpecificKey.State));
				}
				return strategyElements;
			}
			strategyElements.add(renderingStrategy.textComponent("Item", "Item '%s' changed to %s"));
			strategyElements.add(renderingStrategy.itemComponent(itemOrPlaceholder, TriggerTypeSpecificKey.ItemName));
			strategyElements.add(renderingStrategy.textComponent("changed", "Item '%s' changed to %s"));
			strategyElements.add(renderingStrategy.textComponent("to", "Item '%s' changed to %s"));
			if (itemOrPlaceholder.getType().getAllowedStates().length == 1 && itemOrPlaceholder.getType().getAllowedStates()[0] == StateType.Command) {
				strategyElements.add(renderingStrategy.commandComponent(Command.parse(state), TriggerTypeSpecificKey.State));
			} else {
				strategyElements.addAll(renderingStrategy.valueComponent(state, TriggerTypeSpecificKey.State));
			}
			return strategyElements;
		case CommandReceived:
			Command command = (Command) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			SharedItem item2 = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			Collection<T> strategyElements2 = new ArrayList<>();
			strategyElements2.add(renderingStrategy.textComponent("Item", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.itemComponent(getItemOrPlaceholder(item2), TriggerTypeSpecificKey.ItemName));
			strategyElements2.add(renderingStrategy.textComponent("received", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.textComponent("command", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.commandComponent(command, TriggerTypeSpecificKey.Command));
			return strategyElements2;
		case ItemStateUpdated:
			Object item3 = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			SharedItem itemOrPlaceholder3 = getItemOrPlaceholder(item3);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<T> strategyElements3 = new ArrayList<>();

			strategyElements3.add(renderingStrategy.textComponent("Item", "Item '%s' updated to %s"));
			strategyElements3.add(renderingStrategy.itemComponent(itemOrPlaceholder3, TriggerTypeSpecificKey.ItemName));
			strategyElements3.add(renderingStrategy.textComponent("updated", "Item '%s' updated to %s"));
			strategyElements3.add(renderingStrategy.textComponent("to", "Item '%s' updated to %s"));
			if (itemOrPlaceholder3.getType().getAllowedStates().length == 1 && itemOrPlaceholder3.getType().getAllowedStates()[0] == StateType.Command) {
				strategyElements3.add(renderingStrategy.commandComponent(Command.parse(state2), TriggerTypeSpecificKey.State));
			} else {
				strategyElements3.addAll(renderingStrategy.valueComponent(state2, TriggerTypeSpecificKey.State));
			}
			return strategyElements3;
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			Collection<T> strategyElements4 = new ArrayList<>();

			strategyElements4.add(renderingStrategy.textComponent("Time", "Time == %s"));
			strategyElements4.add(renderingStrategy.operationComponent(Operation.EQUAL, ConditionTypeSpecificKey.Operator));
			strategyElements4.addAll(renderingStrategy.valueComponent(time, TriggerTypeSpecificKey.Time));
			return strategyElements4;
		case TriggerChannelFired:
			Object triggerChannel = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			String eventData = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.EventData);

			Collection<T> strategyElements5 = new ArrayList<>();

			strategyElements5.add(renderingStrategy.textComponent("Channel", "Channel '%s' received event '%s'"));
			if (triggerChannel instanceof String) {
				strategyElements5.addAll(renderingStrategy.valueComponent("" + triggerChannel, TriggerTypeSpecificKey.Channel));
			}
			if (triggerChannel instanceof SharedItem) {
				strategyElements5.add(renderingStrategy.itemComponent((SharedItem) triggerChannel, TriggerTypeSpecificKey.Channel));
			}
			strategyElements5.add(renderingStrategy.textComponent("received", "Channel '%s' received event '%s'"));
			strategyElements5.add(renderingStrategy.textComponent("event", "Channel '%s' received event '%s'"));
			strategyElements5.addAll(renderingStrategy.valueComponent(event, TriggerTypeSpecificKey.Event));
			if (eventData != null && !eventData.isEmpty() && !"null".equals(eventData)) {
				strategyElements5.add(renderingStrategy.textComponent("{", "{'%s'}"));
				strategyElements5.addAll(renderingStrategy.valueComponent(eventData, TriggerTypeSpecificKey.EventData));
				strategyElements5.add(renderingStrategy.textComponent("}", "{'%s'}"));
			}
			return strategyElements5;
		default:
			LOG.error("Unsupported trigger type {}.", trigger.getTriggerTypeContainer().getTriggerType());
			return new ArrayList<T>();
		}
	}
}
