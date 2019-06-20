package me.steffenjacobs.iotplatformintegrator.service.homeassistant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class HomeAssistantManualRuleImporter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantManualRuleImporter.class);
	private static final HomeAssistantTriggerTransformationAdapter triggerTransformer = new HomeAssistantTriggerTransformationAdapter();

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
				if (!(o instanceof Map)) {
					LOG.error("not a map:" + o);
					continue;
				}
				@SuppressWarnings("unchecked")
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
								handleTriggerParsingAndResultExtraction(itemDirectory, triggers, conditions, li);
							}
						} else {
							handleTriggerParsingAndResultExtraction(itemDirectory, triggers, conditions, e.getValue());
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

	private void handleTriggerParsingAndResultExtraction(ItemDirectory itemDirectory, Set<SharedTrigger> triggers, Set<SharedCondition> conditions, Object e) {
		Pair<SharedTrigger, Set<SharedCondition>> triggerWithConditions = triggerTransformer.parseTrigger(e, itemDirectory);
		if (triggerWithConditions != null) {
			triggers.add(triggerWithConditions.getLeft());
			conditions.addAll(triggerWithConditions.getRight());
		}
	}

}
