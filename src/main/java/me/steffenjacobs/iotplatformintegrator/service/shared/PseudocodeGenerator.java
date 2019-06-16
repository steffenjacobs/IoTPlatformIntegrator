package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
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

		sb.append("WHEN");
		List<String> triggers = new ArrayList<>();
		for (SharedTrigger trigger : sharedRule.getTriggers()) {
			triggers.add(generateCodeForTrigger(trigger));
		}
		sb.append("\n    ");
		sb.append(String.join("\n    \u2228 ", triggers));
		sb.append("\n\n");

		return sb.toString();
	}

	public String generateCodeForTrigger(SharedTrigger trigger) {

		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateChanged:
			String itemName = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String previousState = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
			String state = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			if (previousState != null && !previousState.equals("") && !previousState.equals("null")) {
				return String.format("Item '%s' changed from '%' to '%s'", itemName, previousState, state);
			}
			return String.format("Item '%s' changed to '%s'", itemName, state);
		case CommandReceived:
			String command = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Command);
			String itemName2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			return String.format("Item '%s' received command '%s'", itemName2, command);
		case ItemStateUpdated:
			String itemName3 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			String state2 = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
			return String.format("Item '%s' was updated to '%s'", itemName3, state2);
		case Timed:
			String time = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time);
			return String.format("Time is equal to '%s'", time);
		case TriggerChannelFired:
			String triggerChannel = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Channel);
			String event = "" + trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Event);
			return String.format("Channel '%s' received event '%s'", triggerChannel, event);
		default:
			LOG.error("Invalid trigger type: {}", trigger.getTriggerTypeContainer().getTriggerType());
			return "<An error occured during parsing.>";
		}
	}

}
