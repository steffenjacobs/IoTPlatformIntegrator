package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;

/** @author Steffen Jacobs */
public class RuleChangeEventStore {
	// TODO: event sourcing
	private final List<RuleChangeEvent> ruleChanges = new ArrayList<>();
	private SharedRule changedRule = null;

	private final Map<RuleChangeEvent, SharedRuleElementDiff> diffMap = new HashMap<>();
	private final RuleDiffService diffService = new RuleDiffService();

	public RuleChangeEventStore() {
		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			final RuleChangeEvent event = (RuleChangeEvent) e;
			if (changedRule != event.getSelectedRule()) {
				changedRule = event.getSelectedRule();
				ruleChanges.clear();
			}
			ruleChanges.add(event);
			diffMap.put(event, diffService.getDiffSharedRuleElement(event.getOldElement(), event.getNewElement()));
		});
	}

	public void applyToRule(SharedRule sharedRule, List<RuleChangeEvent> changes) {
		for (RuleChangeEvent event : changes) {
			if (event.getOldElement() == null) {
				applyAdd(event.getSelectedRule(), event.getNewElement());
			} else if (event.getNewElement() == null) {
				applyRemove(event.getSelectedRule(), event.getOldElement());
			} else {
				applyUpdate(event.getSelectedRule(), event.getOldElement(), event.getNewElement());
			}
		}
	}

	private void applyUpdate(SharedRule rule, SharedRuleElement oldElement, SharedRuleElement newElement) {
		if (oldElement instanceof SharedTrigger) {
			SharedTrigger trigger = (SharedTrigger) oldElement;
			SharedTrigger newTrigger = (SharedTrigger) newElement;
			rule.getTriggers().remove(trigger);
			rule.getTriggers().add(newTrigger);
		} else if (oldElement instanceof SharedCondition) {
			SharedCondition condition = (SharedCondition) oldElement;
			SharedCondition newCondition = (SharedCondition) newElement;
			rule.getConditions().remove(condition);
			rule.getConditions().add(newCondition);
		} else if (oldElement instanceof SharedAction) {
			SharedAction action = (SharedAction) oldElement;
			SharedAction newAction = (SharedAction) newElement;
			rule.getActions().remove(action);
			rule.getActions().add(newAction);
		}
	}

	private void applyAdd(SharedRule rule, SharedRuleElement addedElement) {
		if (addedElement instanceof SharedTrigger) {
			SharedTrigger trigger = (SharedTrigger) addedElement;
			rule.getTriggers().add(trigger);
		} else if (addedElement instanceof SharedCondition) {
			SharedCondition condition = (SharedCondition) addedElement;
			rule.getConditions().add(condition);
		} else if (addedElement instanceof SharedAction) {
			SharedAction action = (SharedAction) addedElement;
			rule.getActions().add(action);
		}
	}

	private void applyRemove(SharedRule rule, SharedRuleElement removedElement) {
		if (removedElement instanceof SharedTrigger) {
			SharedTrigger trigger = (SharedTrigger) removedElement;
			rule.getTriggers().remove(trigger);
		} else if (removedElement instanceof SharedCondition) {
			SharedCondition condition = (SharedCondition) removedElement;
			rule.getConditions().remove(condition);
		} else if (removedElement instanceof SharedAction) {
			SharedAction action = (SharedAction) removedElement;
			rule.getActions().remove(action);
		}
	}

}
