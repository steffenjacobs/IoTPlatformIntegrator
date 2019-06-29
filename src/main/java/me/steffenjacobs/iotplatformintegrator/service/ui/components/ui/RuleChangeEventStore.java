package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;

/** @author Steffen Jacobs */
public class RuleChangeEventStore {
	// TODO: event sourcing
	private static final Logger LOG = LoggerFactory.getLogger(RuleChangeEventStore.class);
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

	public void applyDiff(SharedRule rule, SharedRuleElementDiff diff) {
		if (diff.getElementType() instanceof SharedTrigger) {
			SharedTrigger trigger = getTriggerByRelativeId(rule, diff.getRelativeElementId());
			rule.getTriggers().remove(trigger);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? trigger.getDescription() : diff.getDescription();
			final String label = diff.getLabel() == null ? trigger.getLabel() : diff.getLabel();

			final Map<TriggerTypeSpecificKey, Object> triggerTypeSpecificValues = trigger.getTriggerTypeContainer()
					.getTriggerTypeSpecificValues();

			final Map<String, Object> properties = generalize(triggerTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated());

			SharedTrigger newTrigger = new SharedTrigger(trigger.getTriggerTypeContainer().getTriggerType(), properties,
					description, label, trigger.getRelativeElementId());
			rule.getTriggers().add(newTrigger);

		} else if (diff.getElementType() instanceof SharedCondition) {
			SharedCondition condition = getConditionByRelativeId(rule, diff.getRelativeElementId());
			rule.getConditions().remove(condition);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? condition.getDescription()
					: diff.getDescription();
			final String label = diff.getLabel() == null ? condition.getLabel() : diff.getLabel();

			final Map<ConditionTypeSpecificKey, Object> conditionTypeSpecificValues = condition
					.getConditionTypeContainer().getConditionTypeSpecificValues();
			final Map<String, Object> properties = generalize(conditionTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated());

			SharedCondition newCondition = new SharedCondition(condition.getConditionTypeContainer().getConditionType(),
					properties, description, label, condition.getRelativeElementId());
			rule.getConditions().add(newCondition);
		} else if (diff.getElementType() instanceof SharedAction) {
			SharedAction action = getActionByRelativeId(rule, diff.getRelativeElementId());
			rule.getActions().remove(action);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? action.getDescription() : diff.getDescription();
			final String label = diff.getLabel() == null ? action.getLabel() : diff.getLabel();

			final Map<ActionTypeSpecificKey, Object> actionTypeSpecificValues = action.getActionTypeContainer()
					.getActionTypeSpecificValues();
			final Map<String, Object> properties = generalize(actionTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated());

			SharedAction newAction = new SharedAction(action.getActionTypeContainer().getActionType(), properties,
					description, label, action.getRelativeElementId());
			rule.getActions().add(newAction);
		} else {
			LOG.error("invalid element type {}", diff.getElementType().getClass().getName());
		}
	}

	private Map<String, Object> generalize(Map<? extends SharedTypeSpecificKey, Object> map) {
		final Map<String, Object> result = new HashMap<>();
		for (Entry<? extends SharedTypeSpecificKey, Object> e : map.entrySet()) {
			result.put(e.getKey().getKeyString(), e.getValue());
		}
		return result;

	}

	private void updateMap(Map<String, Object> map, Map<String, Object> toAdd, Map<String, Object> toRemove,
			Map<String, Object> toUpdate) {
		for (Entry<String, Object> entr : toRemove.entrySet()) {
			map.remove(entr.getKey());
		}
		for (Entry<String, Object> entr : toAdd.entrySet()) {
			map.put(entr.getKey(), entr.getValue());
		}
		for (Entry<String, Object> entr : toUpdate.entrySet()) {
			map.remove(entr.getKey());
		}
	}

	private SharedTrigger getTriggerByRelativeId(SharedRule rule, int relativeTriggerId) {
		for (SharedTrigger trigger : rule.getTriggers()) {
			if (trigger.getRelativeElementId() == relativeTriggerId) {
				return trigger;
			}
		}
		return null;
	}

	private SharedCondition getConditionByRelativeId(SharedRule rule, int relativeConditionId) {
		for (SharedCondition condition : rule.getConditions()) {
			if (condition.getRelativeElementId() == relativeConditionId) {
				return condition;
			}
		}
		return null;
	}

	private SharedAction getActionByRelativeId(SharedRule rule, int relativeActionId) {
		for (SharedAction action : rule.getActions()) {
			if (action.getRelativeElementId() == relativeActionId) {
				return action;
			}
		}
		return null;
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
