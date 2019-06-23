package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementRemovedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ActionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ConditionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.TriggerRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.VisualRenderingStrategy;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ActionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.TriggerElement;

/** @author Steffen Jacobs */
public class RuleBuilderRenderController {

	private static final TriggerRenderer<Component> triggerRenderer = new TriggerRenderer<>(new VisualRenderingStrategy());
	private static final ConditionRenderer<Component> conditionRenderer = new ConditionRenderer<>(new VisualRenderingStrategy());
	private static final ActionRenderer<Component> actionRenderer = new ActionRenderer<>(new VisualRenderingStrategy());

	private final RuleBuilder ruleBuilder;

	private final Map<UUID, DynamicElement> renderedRuleElements = new HashMap<>();
	private final Map<UUID, SharedRuleElement> ruleElements = new HashMap<>();
	private SharedRule rule = null;

	public RuleBuilderRenderController(RuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;

		EventBus.getInstance().addEventHandler(EventType.SelectedSourceRuleChanged, e -> renderRule(((SelectedSourceRuleChangeEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RuleElementAdded, e -> addRuleElement(((RuleElementAddedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RuleElementRemoved, e -> removeRuleElement(((RuleElementRemovedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				renderRule(rule);
			}
		});
	}

	private void addRuleElement(UUID sourceId) {
		if (rule != null) {
			SharedRuleElement elem = ruleElements.get(sourceId);
			if (elem instanceof SharedTrigger) {
				rule.getTriggers().add(copy((SharedTrigger) elem));
			} else if (elem instanceof SharedCondition) {
				rule.getConditions().add(copy((SharedCondition) elem));
			} else if (elem instanceof SharedAction) {
				rule.getActions().add(copy((SharedAction) elem));
			}
			EventBus.getInstance().fireEvent(new RuleChangeEvent(rule));
		}
	}

	private SharedTrigger copy(SharedTrigger trigger) {
		Map<String, Object> properties = new HashMap<>();
		trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedTrigger(trigger.getTriggerTypeContainer().getTriggerType(), properties, trigger.getDescription(), trigger.getLabel() + " - Copy");
	}

	private SharedCondition copy(SharedCondition condition) {
		Map<String, Object> properties = new HashMap<>();
		condition.getConditionTypeContainer().getConditionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedCondition(condition.getConditionTypeContainer().getConditionType(), properties, condition.getDescription(), condition.getLabel() + " - Copy");
	}

	private SharedAction copy(SharedAction action) {
		Map<String, Object> properties = new HashMap<>();
		action.getActionTypeContainer().getActionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedAction(action.getActionTypeContainer().getActionType(), properties, action.getDescription(), action.getLabel() + " - Copy");
	}

	private void removeRuleElement(UUID sourceId) {
		if (rule != null) {
			SharedRuleElement elem = ruleElements.remove(sourceId);
			if (elem instanceof SharedTrigger) {
				SharedTrigger trigger = (SharedTrigger) elem;
				rule.getTriggers().remove(trigger);
			} else if (elem instanceof SharedCondition) {
				SharedCondition condition = (SharedCondition) elem;
				rule.getConditions().remove(condition);
			} else if (elem instanceof SharedAction) {
				SharedAction action = (SharedAction) elem;
				rule.getActions().remove(action);
			}
			EventBus.getInstance().fireEvent(new RuleChangeEvent(rule));
		}
	}

	public void renderRule(SharedRule rule) {
		this.rule = rule;
		renderedRuleElements.clear();
		ruleBuilder.clear();
		ruleElements.clear();
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
		UUID uuid = UUID.randomUUID();
		TriggerElement elem = new TriggerElement(uuid);
		elem.setTriggerTypeContainer(trigger.getTriggerTypeContainer());

		String label = trigger.getLabel();
		String description = trigger.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		elem.setStrategyElements(triggerRenderer.renderTrigger(trigger));
		ruleElements.put(uuid, trigger);
		renderedRuleElements.put(uuid, elem);
		return elem;
	}

	private DynamicElement renderAction(SharedAction action, ActionRenderer<Component> actionRenderer) {
		UUID uuid = UUID.randomUUID();
		ActionElement elem = new ActionElement(uuid);
		elem.setActionTypeContainer(action.getActionTypeContainer());

		String label = action.getLabel();
		String description = action.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		elem.setStrategyElements(actionRenderer.renderAction(action));
		renderedRuleElements.put(uuid, elem);
		ruleElements.put(uuid, action);
		return elem;
	}

	private ConditionElement renderCondition(SharedCondition condition, ConditionRenderer<Component> conditionRenderer) {
		UUID uuid = UUID.randomUUID();
		ConditionElement elem = new ConditionElement(uuid);
		elem.setConditionTypeContainer(condition.getConditionTypeContainer());

		String label = condition.getLabel();
		String description = condition.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		elem.setStrategyElements(conditionRenderer.renderCondition(condition));
		ruleElements.put(uuid, condition);
		renderedRuleElements.put(uuid, elem);
		return elem;
	}

}
