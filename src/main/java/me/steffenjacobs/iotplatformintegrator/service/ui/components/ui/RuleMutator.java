package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCopiedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCreatedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementRemovedEvent;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleMutator {

	private final RuleBuilderRenderController ruleBuilderController;

	public RuleMutator(RuleBuilderRenderController ruleBuilderController) {
		this.ruleBuilderController = ruleBuilderController;
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_CREATED,
				e -> createRuleElement(((RuleElementCreatedEvent) e).getElementType(), ((RuleElementCreatedEvent) e).getSharedElementType()));
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_COPIED, e -> copyRuleElement(((RuleElementCopiedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_REMOVED, e -> removeRuleElement(((RuleElementRemovedEvent) e).getSourceId()));
	}

	private void createRuleElement(ElementType elementType, SharedElementType sharedElementType) {
		Optional<SharedRule> oRule = ruleBuilderController.getDisplayedRule();
		oRule.ifPresent(selectedRule -> {
			switch (elementType) {
			case Trigger:
				final Map<String, Object> properties = new HashMap<String, Object>();
				int relativeElementId = findHighestId(selectedRule.getTriggers()) + 1;
				final SharedTrigger trigger = new SharedTrigger((TriggerType) sharedElementType, properties, "Trigger-" + relativeElementId, "-", relativeElementId);
				selectedRule.getTriggers().add(trigger);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, trigger, null));
				break;
			case Condition:
				final Map<String, Object> properties2 = new HashMap<String, Object>();
				int relativeElementId2 = findHighestId(selectedRule.getConditions()) + 1;
				final SharedCondition condition = new SharedCondition((ConditionType) sharedElementType, properties2, "Condition-" + relativeElementId2, "-", relativeElementId2);
				selectedRule.getConditions().add(condition);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, condition, null));
				break;
			case Action:
				final Map<String, Object> properties3 = new HashMap<String, Object>();
				int relativeElementId3 = findHighestId(selectedRule.getActions()) + 1;
				final SharedAction action = new SharedAction((ActionType) sharedElementType, properties3, "Action-" + relativeElementId3, "-", relativeElementId3);
				selectedRule.getActions().add(action);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, action, null));
				break;
			}
		});
	}

	private int findHighestId(Set<? extends SharedRuleElement> set) {
		int highest = 0;
		for (SharedRuleElement t : set) {
			highest = t.getRelativeElementId() > highest ? t.getRelativeElementId() : highest;
		}
		return highest;
	}

	private void copyRuleElement(UUID sourceId) {
		Optional<SharedRule> oRule = ruleBuilderController.getDisplayedRule();
		if (oRule.isPresent()) {
			SharedRule rule = oRule.get();
			SharedRuleElement elem = ruleBuilderController.getRuleElementById(sourceId);
			if (elem instanceof SharedTrigger) {
				rule.getTriggers().add(copy((SharedTrigger) elem));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			} else if (elem instanceof SharedCondition) {
				rule.getConditions().add(copy((SharedCondition) elem));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			} else if (elem instanceof SharedAction) {
				rule.getActions().add(copy((SharedAction) elem));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			}
		}
	}

	private void removeRuleElement(UUID sourceId) {
		Optional<SharedRule> oRule = ruleBuilderController.getDisplayedRule();
		if (oRule.isPresent()) {
			SharedRule rule = oRule.get();
			SharedRuleElement elem = ruleBuilderController.removeRuleElementById(sourceId);
			if (elem instanceof SharedTrigger) {
				SharedTrigger trigger = (SharedTrigger) elem;
				rule.getTriggers().remove(trigger);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, null, elem));
			} else if (elem instanceof SharedCondition) {
				SharedCondition condition = (SharedCondition) elem;
				rule.getConditions().remove(condition);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, null, elem));
			} else if (elem instanceof SharedAction) {
				SharedAction action = (SharedAction) elem;
				rule.getActions().remove(action);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, null, elem));
			}
		}
	}

	private SharedTrigger copy(SharedTrigger trigger) {
		Map<String, Object> properties = new HashMap<>();
		trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedTrigger(trigger.getTriggerTypeContainer().getTriggerType(), properties, trigger.getDescription(), trigger.getLabel() + " - Copy",
				trigger.getRelativeElementId() + 10000);
	}

	private SharedCondition copy(SharedCondition condition) {
		Map<String, Object> properties = new HashMap<>();
		condition.getConditionTypeContainer().getConditionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedCondition(condition.getConditionTypeContainer().getConditionType(), properties, condition.getDescription(), condition.getLabel() + " - Copy",
				condition.getRelativeElementId() + 10000);
	}

	private SharedAction copy(SharedAction action) {
		Map<String, Object> properties = new HashMap<>();
		action.getActionTypeContainer().getActionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedAction(action.getActionTypeContainer().getActionType(), properties, action.getDescription(), action.getLabel() + " - Copy",
				action.getRelativeElementId() + 10000);
	}
}
