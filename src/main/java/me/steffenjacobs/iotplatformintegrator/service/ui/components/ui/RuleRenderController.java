package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ActionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.TriggerElement;

/** @author Steffen Jacobs */
public class RuleRenderController {
	private final RuleBuilder ruleBuilder;

	public RuleRenderController(RuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;

		EventBus.getInstance().addEventHandler(EventType.SelectedSourceRuleChanged, e -> renderRule(((SelectedSourceRuleChangeEvent) e).getSelectedRule()));
	}

	public void renderRule(SharedRule rule) {
		final TriggerRenderer<Component> triggerRenderer = new TriggerRenderer<>(new VisualRenderingStrategy());
		final ConditionRenderer<Component> conditionRenderer = new ConditionRenderer<>(new VisualRenderingStrategy());
		final ActionRenderer<Component> actionRenderer = new ActionRenderer<>(new VisualRenderingStrategy());
		ruleBuilder.clear();
		if (rule == null) {
			return;
		}
		ruleBuilder.setHeader(rule.getName(), rule.getStatus(), rule.getDescription());

		for (SharedTrigger trigger : rule.getTriggers()) {
			ruleBuilder.appendDynamicElement(renderTrigger(trigger, triggerRenderer));
		}
		for (SharedCondition condition : rule.getConditions()) {
			ruleBuilder.appendDynamicElement(renderCondition(condition, conditionRenderer));
		}
		for (SharedAction action : rule.getActions()) {
			ruleBuilder.appendDynamicElement(renderAction(action, actionRenderer));
		}
	}

	private DynamicElement renderTrigger(SharedTrigger trigger, TriggerRenderer<Component> triggerRenderer) {
		TriggerElement elem = new TriggerElement(ruleBuilder);
		elem.setTriggerTypeContainer(trigger.getTriggerTypeContainer());

		String label = trigger.getLabel();
		String description = trigger.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		elem.setStrategyElements(triggerRenderer.renderTrigger(trigger));
		return elem;
	}

	private DynamicElement renderAction(SharedAction action, ActionRenderer<Component> actionRenderer) {
		ActionElement elem = new ActionElement(ruleBuilder);
		elem.setActionTypeContainer(action.getActionTypeContainer());

		String label = action.getLabel();
		String description = action.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		elem.setStrategyElements(actionRenderer.renderAction(action));
		return elem;
	}

	private ConditionElement renderCondition(SharedCondition condition, ConditionRenderer<Component> conditionRenderer) {
		ConditionElement elem = new ConditionElement(ruleBuilder);
		elem.setConditionTypeContainer(condition.getConditionTypeContainer());

		String label = condition.getLabel();
		String description = condition.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		elem.setStrategyElements(conditionRenderer.renderCondition(condition));
		return elem;
	}

}
