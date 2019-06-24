package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;

/** @author Steffen Jacobs */
public class RuleDiffService {
	public SharedRuleElementDiff getDiffSharedRuleElement(SharedRuleElement oldElement, SharedRuleElement newElement) {
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
}
