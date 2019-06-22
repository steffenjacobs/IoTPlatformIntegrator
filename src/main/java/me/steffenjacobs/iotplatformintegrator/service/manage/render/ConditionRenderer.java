package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;

/** @author Steffen Jacobs */
public class ConditionRenderer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ConditionRenderer.class);

	private final RenderingStrategy<T> renderingStrategy;

	public ConditionRenderer(RenderingStrategy<T> renderingStrategy) {
		this.renderingStrategy = renderingStrategy;
	}

	public Collection<T> renderCondition(SharedCondition condition) {
		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ScriptEvaluatesTrue:
			List<T> tokens = new ArrayList<>();
			String script = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Script);
			String type = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Type);
			tokens.add(renderingStrategy.textComponent("Script", "Script %s {%s} evaluates to true"));
			tokens.addAll(renderingStrategy.valueComponent(type));
			tokens.add(renderingStrategy.textComponent("{", "{"));
			tokens.addAll(renderingStrategy.valueComponent(script));
			tokens.add(renderingStrategy.textComponent("}", "}"));
			tokens.add(renderingStrategy.textComponent("evaluates", "Script %s {%s} evaluates to true"));
			tokens.add(renderingStrategy.textComponent("to", "Script %s {%s} evaluates to true"));
			tokens.add(renderingStrategy.textComponent("true", "Script %s {%s} evaluates to true"));
			return tokens;
		case ItemState:
			SharedItem item = (SharedItem) condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName);
			String state = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.State);
			Operation operator = (Operation) condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.Operator);
			List<T> tokens2 = new ArrayList<>();
			tokens2.add(renderingStrategy.textComponent("value", "value of item '%s' %s %s"));
			tokens2.add(renderingStrategy.textComponent("of", "value of item '%s' %s %s"));
			tokens2.add(renderingStrategy.textComponent("item", "value of item '%s' %s %s"));
			if (item != null) {
				tokens2.add(renderingStrategy.itemComponent(item));
			} else {
				tokens2.add(renderingStrategy.textComponent("<null item>", "<null item>"));
			}
			tokens2.add(renderingStrategy.operationComponent(operator));
			tokens2.addAll(renderingStrategy.valueComponent(state));
			return tokens2;
		case DayOfWeek:
			return Arrays.asList(renderingStrategy.textComponent("<Day of Week is not implemented with openHAB 2.4.0>", "<Day of Week is not implemented with openHAB 2.4.0>"));
		case TimeOfDay:
			String startTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.StartTime);
			String endTime = "" + condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.EndTime);
			List<T> tokens3 = new ArrayList<>();
			tokens3.add(renderingStrategy.textComponent("time", "time between %s and %s"));
			tokens3.add(renderingStrategy.textComponent("between", "time between %s and %s"));
			tokens3.addAll(renderingStrategy.valueComponent(startTime));
			tokens3.add(renderingStrategy.textComponent("and", "time between %s and %s"));
			tokens3.addAll(renderingStrategy.valueComponent(endTime));
			return tokens3;
		default:
			LOG.error("Invalid condition type: {}", condition.getConditionTypeContainer().getConditionType());
			return Arrays.asList(renderingStrategy.textComponent("<An error occured during parsing of the condition.>", "<An error occured during parsing of the condition.>"));
		}
	}

}
