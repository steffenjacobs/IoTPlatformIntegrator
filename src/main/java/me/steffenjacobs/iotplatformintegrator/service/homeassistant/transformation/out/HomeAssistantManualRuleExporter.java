package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;

/** @author Steffen Jacobs */
public class HomeAssistantManualRuleExporter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantManualRuleExporter.class);

	private static final HomeAssistantReverseTriggerTransformationAdapter reverseTriggerTransformer = new HomeAssistantReverseTriggerTransformationAdapter();
	private static final HomeAssistantReverseConditionTransformationAdapter reverseConditionTransformer = new HomeAssistantReverseConditionTransformationAdapter();
	private static final HomeAssistantActionReverseTransformationAdapter reverseActionTransformer = new HomeAssistantActionReverseTransformationAdapter();

	
	public void exportRule(SharedRule rule, File file) {

		final Map<String, Object> outputYamlMap = new HashMap<>();
		outputYamlMap.put("alias", rule.getName());
		outputYamlMap.put("id", rule.getId());
		outputYamlMap.put("id", rule.getId());
		outputYamlMap.put("trigger", reverseParseTriggers(rule));
		outputYamlMap.put("condition", reverseParseConditions(rule));
		outputYamlMap.put("action", reverseParseActions(rule));

		// write to file in YAML
		final Yaml yaml = new Yaml();
		try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
			yaml.dump(outputYamlMap, w);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		JOptionPane.showMessageDialog(null, "Rule Export finished",
				String.format("Do not forget to copy the rules exported to %s into the associated file within your HomeAssistant instance!", file.getAbsolutePath()),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private Object reverseParseActions(SharedRule rule) {
		if (rule.getActions().size() > 1) {
			List<Object> result = new ArrayList<>();
			rule.getActions().forEach(t -> result.add(reverseActionTransformer.parseAction(t)));
			return result;
		} else if (rule.getActions().size() == 0) {
			return "";
		} else {
			return reverseActionTransformer.parseAction(rule.getActions().iterator().next());
		}
	}

	private Object reverseParseConditions(SharedRule rule) {
		if (rule.getConditions().size() > 1) {
			List<Object> result = new ArrayList<>();
			rule.getConditions().forEach(t -> result.add(reverseConditionTransformer.parseCondition(t)));
			return result;
		} else if (rule.getConditions().size() == 0) {
			return "";
		} else {
			return reverseConditionTransformer.parseCondition(rule.getConditions().iterator().next());
		}
	}

	private Object reverseParseTriggers(SharedRule rule) {
		if (rule.getTriggers().size() > 1) {
			List<Object> result = new ArrayList<>();
			rule.getTriggers().forEach(t -> result.add(reverseTriggerTransformer.parseTrigger(t)));
			return result;
		} else if (rule.getTriggers().size() == 0) {
			return "";
		} else {
			return reverseTriggerTransformer.parseTrigger(rule.getTriggers().iterator().next());
		}
	}

}
