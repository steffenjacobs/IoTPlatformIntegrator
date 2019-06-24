package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
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
	private final List<RuleChangeEvent> ruleChanges = new ArrayList<>();
	private SharedRule changedRule = null;

	private final Map<RuleChangeEvent, SharedRuleElementDiff> diffMap = new HashMap<>();

	public RuleChangeEventStore() {
		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			final RuleChangeEvent event = (RuleChangeEvent) e;
			if (changedRule != event.getSelectedRule()) {
				changedRule = event.getSelectedRule();
				ruleChanges.clear();
			}
			ruleChanges.add(event);
			diffMap.put(event, getDiffSharedRuleElement(event.getOldElement(), event.getNewElement()));
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

	private SharedRuleElementDiff getDiffSharedRuleElement(SharedRuleElement oldElement, SharedRuleElement newElement) {
		final SharedRuleElementDiff diffElement = null;
		final String description;
		if (!oldElement.getDescription().equals(newElement.getDescription())) {
			description = newElement.getDescription();
		} else {
			description = null;
		}

		final String label;
		if (!oldElement.getLabel().contentEquals(newElement.getLabel())) {
			label = newElement.getLabel();
		} else {
			label = null;
		}

		if (oldElement instanceof SharedTrigger) {
			SharedTrigger oldTrigger = (SharedTrigger) oldElement;
			SharedTrigger newTrigger = (SharedTrigger) newElement;
			final SharedElementType elementType;

			if (oldTrigger.getTriggerTypeContainer().getTriggerType() != newTrigger.getTriggerTypeContainer().getTriggerType()) {
				elementType = newTrigger.getTriggerTypeContainer().getTriggerType();
			} else {
				elementType = null;
			}

			Map<String, Object> propertiesRemoved = new HashMap<>();
			Map<String, Object> propertiesAdded = new HashMap<>();
			Map<String, Object> propertiesUpdated = new HashMap<>();

			for (TriggerTypeSpecificKey key : newTrigger.getTriggerTypeContainer().getTriggerType().getTypeSpecificKeys()) {
				Object oldValue = oldTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(key);
				Object newValue = newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(key);
				if (oldValue == null) {
					propertiesAdded.put(key.getKeyString(), newValue);
				} else if (!oldValue.equals(newValue)) {
					propertiesUpdated.put(key.getKeyString(), newValue);
				}
			}

			for (TriggerTypeSpecificKey key : oldTrigger.getTriggerTypeContainer().getTriggerType().getTypeSpecificKeys()) {
				Object oldValue = oldTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(key);
				Object newValue = newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(key);
				if (newValue == null) {
					propertiesRemoved.put(key.getKeyString(), oldValue);
				}
			}

			return new SharedRuleElementDiff(description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated);
		} else if (oldElement instanceof SharedCondition) {
			SharedCondition oldCondition = (SharedCondition) oldElement;
			SharedCondition newCondition = (SharedCondition) newElement;
			final SharedElementType elementType;

			if (oldCondition.getConditionTypeContainer().getConditionType() != newCondition.getConditionTypeContainer().getConditionType()) {
				elementType = newCondition.getConditionTypeContainer().getConditionType();
			} else {
				elementType = null;
			}

			Map<String, Object> propertiesRemoved = new HashMap<>();
			Map<String, Object> propertiesAdded = new HashMap<>();
			Map<String, Object> propertiesUpdated = new HashMap<>();

			for (ConditionTypeSpecificKey key : newCondition.getConditionTypeContainer().getConditionType().getTypeSpecificKeys()) {
				Object oldValue = oldCondition.getConditionTypeContainer().getConditionTypeSpecificValues().get(key);
				Object newValue = newCondition.getConditionTypeContainer().getConditionTypeSpecificValues().get(key);
				if (oldValue == null) {
					propertiesAdded.put(key.getKeyString(), newValue);
				} else if (!oldValue.equals(newValue)) {
					propertiesUpdated.put(key.getKeyString(), newValue);
				}
			}

			for (ConditionTypeSpecificKey key : oldCondition.getConditionTypeContainer().getConditionType().getTypeSpecificKeys()) {
				Object oldValue = oldCondition.getConditionTypeContainer().getConditionTypeSpecificValues().get(key);
				Object newValue = newCondition.getConditionTypeContainer().getConditionTypeSpecificValues().get(key);
				if (newValue == null) {
					propertiesRemoved.put(key.getKeyString(), oldValue);
				}
			}

			return new SharedRuleElementDiff(description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated);
		} else if (oldElement instanceof SharedAction) {
			SharedAction oldAction = (SharedAction) oldElement;
			SharedAction newAction = (SharedAction) newElement;
			final SharedElementType elementType;

			if (oldAction.getActionTypeContainer().getActionType() != newAction.getActionTypeContainer().getActionType()) {
				elementType = newAction.getActionTypeContainer().getActionType();
			} else {
				elementType = null;
			}

			Map<String, Object> propertiesRemoved = new HashMap<>();
			Map<String, Object> propertiesAdded = new HashMap<>();
			Map<String, Object> propertiesUpdated = new HashMap<>();

			for (ActionTypeSpecificKey key : newAction.getActionTypeContainer().getActionType().getTypeSpecificKeys()) {
				Object oldValue = oldAction.getActionTypeContainer().getActionTypeSpecificValues().get(key);
				Object newValue = newAction.getActionTypeContainer().getActionTypeSpecificValues().get(key);
				if (oldValue == null) {
					propertiesAdded.put(key.getKeyString(), newValue);
				} else if (!oldValue.equals(newValue)) {
					propertiesUpdated.put(key.getKeyString(), newValue);
				}
			}

			for (ActionTypeSpecificKey key : oldAction.getActionTypeContainer().getActionType().getTypeSpecificKeys()) {
				Object oldValue = oldAction.getActionTypeContainer().getActionTypeSpecificValues().get(key);
				Object newValue = newAction.getActionTypeContainer().getActionTypeSpecificValues().get(key);
				if (newValue == null) {
					propertiesRemoved.put(key.getKeyString(), oldValue);
				}
			}

			return new SharedRuleElementDiff(description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated);
		}
		return diffElement;
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
