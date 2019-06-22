package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class TriggerRenderer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(TriggerRenderer.class);

	private final RenderingStrategy<T> renderingStrategy;

	public TriggerRenderer(RenderingStrategy<T> renderingStrategy) {
		this.renderingStrategy = renderingStrategy;

	}

	public Collection<T> renderTrigger(SharedTrigger trigger) {
		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			Object item = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<T> strategyElements = new ArrayList<>();

			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				strategyElements.add(renderingStrategy.textComponent("Item", "Item '%s' changed from %s to %s"));
				strategyElements.add(renderingStrategy.itemComponent(getItemOrPlaceholder(item)));
				strategyElements.add(renderingStrategy.textComponent("changed", "Item '%s' changed from %s to %s"));
				strategyElements.addAll(renderingStrategy.valueComponent(previousState));
				strategyElements.add(renderingStrategy.textComponent("from", "Item '%s' changed from %s to %s"));
				strategyElements.addAll(renderingStrategy.valueComponent(state));
				strategyElements.add(renderingStrategy.textComponent("to", "Item '%s' changed from %s to %s"));
				return strategyElements;
			}
			strategyElements.add(renderingStrategy.textComponent("Item", "Item '%s' changed to %s"));
			strategyElements.add(renderingStrategy.itemComponent(getItemOrPlaceholder(item)));
			strategyElements.add(renderingStrategy.textComponent("changed", "Item '%s' changed to %s"));
			strategyElements.add(renderingStrategy.textComponent("to", "Item '%s' changed to %s"));
			strategyElements.addAll(renderingStrategy.valueComponent(state));
			return strategyElements;
		case CommandReceived:
			Command command = (Command) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			SharedItem item2 = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			Collection<T> strategyElements2 = new ArrayList<>();
			strategyElements2.add(renderingStrategy.textComponent("Item", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.itemComponent(getItemOrPlaceholder(item2)));
			strategyElements2.add(renderingStrategy.textComponent("received", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.textComponent("command", "Item '%s' received command '%s'"));
			strategyElements2.add(renderingStrategy.commandComponent(command));
			return strategyElements2;
		case ItemStateUpdated:
			Object item3 = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);

			Collection<T> strategyElements3 = new ArrayList<>();

			strategyElements3.add(renderingStrategy.textComponent("Item", "Item '%s' updated to %s"));
			strategyElements3.add(renderingStrategy.itemComponent(getItemOrPlaceholder(item3)));
			strategyElements3.add(renderingStrategy.textComponent("updated", "Item '%s' updated to %s"));
			strategyElements3.add(renderingStrategy.textComponent("to", "Item '%s' updated to %s"));
			strategyElements3.addAll(renderingStrategy.valueComponent(state2));
			return strategyElements3;
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			Collection<T> strategyElements4 = new ArrayList<>();

			strategyElements4.add(renderingStrategy.textComponent("Time", "Time == %s"));
			strategyElements4.add(renderingStrategy.operationComponent(Operation.EQUAL));
			strategyElements4.addAll(renderingStrategy.valueComponent(time));
			return strategyElements4;
		case TriggerChannelFired:
			Object triggerChannel = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			String eventData = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.EventData);

			Collection<T> strategyElements5 = new ArrayList<>();

			strategyElements5.add(renderingStrategy.textComponent("Channel", "Channel '%s' received event '%s'"));
			if (triggerChannel instanceof String) {
				strategyElements5.addAll(renderingStrategy.valueComponent("" + triggerChannel));
			}
			if (triggerChannel instanceof SharedItem) {
				strategyElements5.add(renderingStrategy.itemComponent((SharedItem) triggerChannel));
			}
			strategyElements5.add(renderingStrategy.textComponent("received", "Channel '%s' received event '%s'"));
			strategyElements5.add(renderingStrategy.textComponent("event", "Channel '%s' received event '%s'"));
			strategyElements5.addAll(renderingStrategy.valueComponent(event));
			if (eventData != null && !eventData.isEmpty() && !"null".equals(eventData)) {
				strategyElements5.add(renderingStrategy.textComponent("{", "{'%s'}"));
				strategyElements5.addAll(renderingStrategy.valueComponent(eventData));
				strategyElements5.add(renderingStrategy.textComponent("}", "{'%s'}"));
			}
			return strategyElements5;
		default:
			LOG.error("Unsupported trigger type {}.", trigger.getTriggerTypeContainer().getTriggerType());
			return new ArrayList<T>();
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
}
