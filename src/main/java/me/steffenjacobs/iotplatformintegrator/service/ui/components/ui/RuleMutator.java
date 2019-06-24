package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

/** @author Steffen Jacobs */
public class RuleMutator {

	private final RuleBuilderRenderController ruleBuilderController;

	public RuleMutator(RuleBuilderRenderController ruleBuilderController) {
		this.ruleBuilderController = ruleBuilderController;
		EventBus.getInstance().addEventHandler(EventType.RuleElementAdded, e -> addRuleElement(((RuleElementAddedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RuleElementRemoved, e -> removeRuleElement(((RuleElementRemovedEvent) e).getSourceId()));
	}

	private void addRuleElement(UUID sourceId) {
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
}
