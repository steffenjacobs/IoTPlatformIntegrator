package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.ui.util.StringUtil;

/** @author Steffen Jacobs */
public class HomeAssistantTriggerTransformationAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantTriggerTransformationAdapter.class);

	public Pair<SharedTrigger, Set<SharedCondition>> parseTrigger(Object o, ItemDirectory itemDirectory) {
		if (!(o instanceof Map)) {
			System.out.println(o);
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) o;

		TriggerType triggerType = parseTriggerType("" + map.get("platform"));
		if (triggerType == null) {
			LOG.error("Trigger type not found! platform attribute missing");
			return null;
		}

		Set<SharedCondition> conditions = new HashSet<>();
		Map<String, Object> properties = new HashMap<>();
		switch (triggerType) {
		case ItemStateChanged:

			if (map.get("platform").equals("numeric_state")) {

				String below = "" + map.get("below");
				String above = "" + map.get("above");
				String itemName = "" + map.get("entity_id");

				if (StringUtil.isNonNull(below)) {
					// TODO: fix label + description
					Map<String, Object> conditionProperties = new HashMap<>();
					conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.SMALLER);
					conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), below);
					String description = itemName + " below " + below;
					String label = ConditionType.ItemState + " below condition";
					SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
					conditions.add(sc);
				}
				if (StringUtil.isNonNull(above)) {
					// TODO: fix label + description
					Map<String, Object> conditionProperties = new HashMap<>();
					conditionProperties.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.BIGGER);
					conditionProperties.put(ConditionTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));
					conditionProperties.put(ConditionTypeSpecificKey.State.getKeyString(), above);
					String description = itemName + " above " + above;
					String label = ConditionType.ItemState + " above condition";
					SharedCondition sc = new SharedCondition(ConditionType.ItemState, conditionProperties, description, label);
					conditions.add(sc);
				}

				properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), itemDirectory.getItemByName(itemName));

				// optional: TODO: Implement
				// String forr = "" + map.get("for");
			} else if (map.get("platform").equals("state")) {
				Object from = map.get("from");
				String to = "" + map.get("to");
				SharedItem item = itemDirectory.getItemByName("" + map.get("entity_id"));
				properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), item);

				Command cmdFrom = Command.parse("" + from);
				if (cmdFrom != Command.Unknown) {
					properties.put(TriggerTypeSpecificKey.PreviousState.getKeyString(), cmdFrom);
				} else {
					properties.put(TriggerTypeSpecificKey.PreviousState.getKeyString(), from);
				}

				Command cmdTo = Command.parse("" + to);
				if (cmdTo != Command.Unknown) {
					properties.put(TriggerTypeSpecificKey.State.getKeyString(), cmdTo);
				} else {
					properties.put(TriggerTypeSpecificKey.State.getKeyString(), to);
				}

			}
			break;
		case TriggerChannelFired:
			if (map.get("platform").equals("geo_location")) {
				// parse GeoEvent
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
				String itemName2 = "" + map.get("source");
				SharedItem itemByName = itemDirectory.getItemByName(itemName2);
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemByName != null ? itemByName : itemName2);
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("zone"));
			} else if (map.get("platform").equals("zone")) {
				// home zone event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemDirectory.getItemByName("" + map.get("entity_id")));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("zone"));
			} else if (map.get("platform").equals("homeassistant")) {
				// home assistant event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemDirectory.getItemByName("homeassistant.instance"));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
			} else if (map.get("platform").equals("mqtt")) {
				// MQTT event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), map.get("topic"));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("payload"));
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("MQTT"));
			} else if (map.get("platform").equals("sun")) {
				// sun event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemDirectory.getItemByName("sun.sun"));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), "offset=" + map.get("offset"));
			} else if (map.get("platform").equals("webhook")) {
				// webhook event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), "Webhook #" + map.get("webhook_id"));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("Webhook-Event"));
			} else {
				// parse generic "Event"
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event_type"));
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("event_data"));
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemDirectory.getItemByName("homeassistant.instance"));
			}
			break;
		case Timed:
			if (map.get("platform").equals("time")) {
				// at xx:xx
				properties.put(TriggerTypeSpecificKey.Time.getKeyString(), map.get("at"));
			} else {
				// in xx:xx:xx hours/mins/seconds
				// TODO: improve support for relative time
				properties.put(TriggerTypeSpecificKey.Time.getKeyString(), String.format("now+%s:%s:%S", map.get("hours"), map.get("minutes"), map.get("seconds")));
			}
			break;
		default:
			LOG.error("trigger type not implemented: " + triggerType);
		}
		String description = "";
		String label = "";
		SharedTrigger st = new SharedTrigger(triggerType, properties, description, label);

		return Pair.of(st, conditions);
	}

	private TriggerType parseTriggerType(String triggerTypeString) {
		switch (triggerTypeString) {
		case "numeric_state":
		case "state":
			return TriggerType.ItemStateChanged;
		case "event":
			return TriggerType.TriggerChannelFired;
		case "geo_location":
		case "zone":
			return TriggerType.TriggerChannelFired;
		case "homeassistant":
			return TriggerType.TriggerChannelFired;
		case "mqtt":
			return TriggerType.TriggerChannelFired;
		case "sun":
			return TriggerType.TriggerChannelFired;
		case "time":
		case "time_pattern":
			return TriggerType.Timed;
		case "webhook":
			return TriggerType.TriggerChannelFired;

		default:
			LOG.error("invalid trigger type: " + triggerTypeString);
			return TriggerType.Unknown;
		}

	}
}
