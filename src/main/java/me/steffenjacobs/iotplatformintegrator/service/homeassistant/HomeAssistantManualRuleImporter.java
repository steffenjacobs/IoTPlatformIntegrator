package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class HomeAssistantManualRuleImporter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantManualRuleImporter.class);

	public List<SharedRule> importRules(ItemDirectory itemDirectory) {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Empty rules detected. Do you want to import data for the missing rules?", "Warning",
				JOptionPane.YES_NO_OPTION)) {

		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return importRules(selectedFile, itemDirectory);
		}
		return new ArrayList<SharedRule>();
	}

	public List<SharedRule> importRules(File file, ItemDirectory itemDirectory) {
		List<SharedRule> rules = new ArrayList<SharedRule>();
		try {
			String yamlString = new String(Files.readAllBytes(file.toPath()), "UTF-8");
			Yaml yaml = new Yaml();
			ArrayList<Object> list = yaml.load(yamlString);

			for (Object o : list) {
				String description = "";
				String id = "";
				String name = "";

				// need to be requested via REST API
				String visible = "";
				String status = "";
				Set<SharedTrigger> triggers = new HashSet<>();
				Set<SharedCondition> conditions = new HashSet<>();
				Set<SharedAction> actions = new HashSet<>();
				Map<String, Object> map = (Map<String, Object>) o;
				for (Entry<String, Object> e : map.entrySet()) {
					switch (e.getKey()) {
					case "alias":
						description = "" + e.getValue();
						name = "" + e.getValue();
						break;
					case "id":
						id = "" + e.getValue();
						break;
					case "trigger":
						if (e.getValue() instanceof List) {
							for (Object li : (Iterable<?>) e.getValue()) {
								Pair<SharedTrigger, Set<SharedCondition>> triggerWithConditions = parseTrigger(li, itemDirectory);
								if (triggerWithConditions != null) {
									triggers.add(triggerWithConditions.getLeft());
									conditions.addAll(triggerWithConditions.getRight());
								}
							}
						} else {
							Pair<SharedTrigger, Set<SharedCondition>> triggerWithConditions = parseTrigger(e.getValue(), itemDirectory);
							if (triggerWithConditions != null) {
								triggers.add(triggerWithConditions.getLeft());
								conditions.addAll(triggerWithConditions.getRight());
							}
						}
						break;
					case "condition":
					case "action":
					}
					System.out.println(e);
				}
				rules.add(new SharedRule(name, id, description, visible, status, triggers, conditions, actions));
				System.out.println(map);
			}
			System.out.println(list);
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		return rules;
	}

	private Pair<SharedTrigger, Set<SharedCondition>> parseTrigger(Object o, ItemDirectory itemDirectory) {
		if (!(o instanceof Map)) {
			System.out.println(o);
			return null;
		}
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

			String below = "" + map.get("below");
			String above = "" + map.get("above");
			String itemName = "" + map.get("entity_id");

			if (isNonNull(below)) {
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
			if (isNonNull(above)) {
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
			String forr = "" + map.get("for");
			break;
		case TriggerChannelFired:
			if (map.get("platform").equals("geo_location")) {
				// parse GeoEvent
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
				String itemName2 = "" + map.get("source");
				SharedItem itemByName = itemDirectory.getItemByName(itemName2);
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemByName != null ? itemByName : itemName2);
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("zone"));
			} else if (map.get("platform").equals("homeassistant")) {
				// home assistant event
				properties.put(TriggerTypeSpecificKey.Channel.getKeyString(), itemDirectory.getItemByName("homeassistant.instance"));
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event"));
			} else {
				// parse "Event" or "homeassistant
				properties.put(TriggerTypeSpecificKey.Event.getKeyString(), map.get("event_type"));
				properties.put(TriggerTypeSpecificKey.EventData.getKeyString(), map.get("event_data"));
			}
			break;

		default:
			System.out.println("not implemented");
		}
		String description = "";
		String label = "";
		SharedTrigger st = new SharedTrigger(triggerType, properties, description, label);

		return Pair.of(st, conditions);
	}

	private boolean isNonNull(String s) {
		return s != null && !s.isEmpty() && !s.equals("null");
	}

	private TriggerType parseTriggerType(String triggerTypeString) {
		switch (triggerTypeString) {
		case "numeric_state":
			return TriggerType.ItemStateChanged;
		case "event":
			return TriggerType.TriggerChannelFired;
		case "geo_location":
			return TriggerType.TriggerChannelFired;
		case "homeassistant":
			return TriggerType.TriggerChannelFired;
		default:
			return TriggerType.Unknown;
		}

	}

}
