package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;

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

		for (SharedCondition condition : rule.getConditions()) {
			ruleBuilder.appendConditionElement(renderCondition(condition));
		}
	}

	private ConditionElement renderCondition(SharedCondition condition) {
		String label = condition.getLabel();
		String description = condition.getDescription();
		ConditionElement elem = new ConditionElement(ruleBuilder);

		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ItemState:
			elem.setConditionTypeContainer(condition.getConditionTypeContainer());
			break;
		default:
			LOG.error("Unsupported condition type {}.", condition.getConditionTypeContainer().getConditionType());
		}
		return elem;
	}

}
