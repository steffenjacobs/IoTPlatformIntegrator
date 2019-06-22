package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import javax.swing.JPanel;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;

/** @author Steffen Jacobs */
public class RuleRenderController {

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
		return new ConditionElement();
	}

	public static class ConditionElement extends JPanel {
		private static final long serialVersionUID = 262937647608306926L;

	}

}
