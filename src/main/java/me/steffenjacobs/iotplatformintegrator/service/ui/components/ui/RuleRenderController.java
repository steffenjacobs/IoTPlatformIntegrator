package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
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
	private static final Logger LOG = LoggerFactory.getLogger(RuleRenderController.class);

	private final RuleBuilder ruleBuilder;

	public RuleRenderController(RuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;

		EventBus.getInstance().addEventHandler(EventType.SelectedSourceRuleChanged, e -> renderRule(((SelectedSourceRuleChangeEvent) e).getSelectedRule()));
	}

	public void renderRule(SharedRule rule) {
		final TriggerRenderer<Component> triggerRenderer = new TriggerRenderer<>(new VisualRenderingStrategy(), this::getItemOrPlaceholder);
		ruleBuilder.clear();
		if (rule == null) {
			return;
		}
		ruleBuilder.setHeader(rule.getName(), rule.getStatus(), rule.getDescription());

		for (SharedTrigger trigger : rule.getTriggers()) {
			ruleBuilder.appendDynamicElement(renderTrigger(trigger, triggerRenderer));
		}
		for (SharedCondition condition : rule.getConditions()) {
			ruleBuilder.appendDynamicElement(renderCondition(condition));
		}
		for (SharedAction action : rule.getActions()) {
			ruleBuilder.appendDynamicElement(renderAction(action));
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

	private SharedItem getItemOrPlaceholder(Object item) {
		if (item instanceof SharedItem) {
			return (SharedItem) item;
		} else if (item instanceof String) {
			return new SharedItem("<invalid item name '" + item + "'>", "<invalid item name '" + item + "'>", ItemType.Unknown);
		} else {
			return new SharedItem("<null item>", "<null item>", ItemType.Unknown);
		}
	}

	private DynamicElement renderAction(SharedAction action) {
		ActionElement elem = new ActionElement(ruleBuilder);
		elem.setActionTypeContainer(action.getActionTypeContainer());

		String label = action.getLabel();
		String description = action.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		switch (action.getActionTypeContainer().getActionType()) {
		default:
			LOG.error("Unsupported Action type {}.", action.getActionTypeContainer().getActionType());
		}
		return elem;
	}

	private ConditionElement renderCondition(SharedCondition condition) {
		ConditionElement elem = new ConditionElement(ruleBuilder);
		elem.setConditionTypeContainer(condition.getConditionTypeContainer());

		String label = condition.getLabel();
		String description = condition.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		switch (condition.getConditionTypeContainer().getConditionType()) {
		case ItemState:
			break;
		default:
			LOG.error("Unsupported condition type {}.", condition.getConditionTypeContainer().getConditionType());
		}
		return elem;
	}

}
