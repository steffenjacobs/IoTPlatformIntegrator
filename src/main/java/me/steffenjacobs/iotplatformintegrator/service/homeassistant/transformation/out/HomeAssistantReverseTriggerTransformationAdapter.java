package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.out;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.DataType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class HomeAssistantReverseTriggerTransformationAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantReverseTriggerTransformationAdapter.class);

	public Object parseTrigger(SharedTrigger trigger) {
		final Map<String, Object> result = new HashMap<>();
		result.put("platform", parseTriggerType(trigger.getTriggerTypeContainer().getTriggerType()));

		switch (trigger.getTriggerTypeContainer().getTriggerType()) {
		case ItemStateUpdated:
		case ItemStateChanged:
		case CommandReceived:
			final SharedItem item = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
			result.put("entity_id", item.getName());

			if (item.getType().getDatatype() == DataType.Numerical) {
				// nothing to do -> below/above are mapped via conditions
			} else {
				final Object prevState = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.PreviousState);
				if (prevState != null) {
					if (prevState instanceof Command) {
						result.put("from", ((Command) prevState).name());
					} else {
						result.put("from", prevState);
					}
				}
				final Object state = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State);
				if (state != null) {
					if (state instanceof Command) {
						result.put("to", ((Command) prevState).name());
					} else {
						result.put("to", state);
					}
				}

			}
			break;
		case Timed:
			result.put("at", trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.Time));
			break;
		case GeoLocationChanged:
		case TriggerChannelFired:
		case Unknown:
			LOG.error("Error could not reverse transform trigger: " + trigger.getLabel());
			break;
		}

		return result;
	}

	private String parseTriggerType(TriggerType type) {
		switch (type) {
		case ItemStateUpdated:
		case ItemStateChanged:
		case CommandReceived:
			return "state";
		case GeoLocationChanged:
		case TriggerChannelFired:
			return "event";
		case Timed:
			return "time";
		default:
			return "";
		}
	}
}
