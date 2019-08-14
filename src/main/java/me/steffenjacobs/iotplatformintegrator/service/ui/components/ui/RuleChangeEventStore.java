package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationService;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;

/** @author Steffen Jacobs */
public class RuleChangeEventStore {
	// TODO: event sourcing
	private static final Logger LOG = LoggerFactory.getLogger(RuleChangeEventStore.class);
	private final List<RuleChangeEvent> ruleChanges = new ArrayList<>();
	private SharedRule changedRule = null;

	private final Map<RuleChangeEvent, SharedRuleElementDiff> diffMap = new HashMap<>();
	private final RuleDiffService diffService = new RuleDiffService();

	public RuleChangeEventStore(AuthenticationService authenticationService) {
		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			final RuleChangeEvent event = (RuleChangeEvent) e;
			if (changedRule != event.getSelectedRule()) {
				changedRule = event.getSelectedRule();
				ruleChanges.clear();
			}
			ruleChanges.add(event);
			SharedRuleElementDiff calculatedDiff = diffService.getDiffSharedRuleElement(event.getOldElement(), event.getNewElement());
			diffMap.put(event, calculatedDiff);
			if (authenticationService.isLoginSuccessful()) {
				EventBus.getInstance().fireEvent(new RuleDiffChangeEvent(event.getSelectedRule(), calculatedDiff, authenticationService.getCurrentUser().getUserId().toString()));
			} else {
				JOptionPane.showMessageDialog(new JDialog(), "Invalid user credentials. Please restart the application or provide correct credentials under File -> Settings",
						"Invalid user credentials.", JOptionPane.ERROR_MESSAGE);
			}
		});

		EventBus.getInstance().addEventHandler(EventType.SelectedRuleDiffChanged, e -> {
			final SharedRule rebuildRule = rebuildRule(((SelectedRuleDiffChangeEvent) e).getRuleDiffParts());
			EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(rebuildRule));

		});
	}

	private SharedRule rebuildRule(RuleDiffParts parts) {
		if (parts.getPrevDiffId() == null) {
			final SharedRule ruleByName = App.getRemoteRuleCache().getRuleByName(parts.getSourceRuleName());
			final SharedRule sharedRuleCopy = new SharedRule(ruleByName.getName(), ruleByName);
			
			applyDiff(sharedRuleCopy, parts.getRuleDiff(), App.getDatabaseConnectionObject().getItemDirectory());
			return sharedRuleCopy;
		} else {
			final SharedRule rule = rebuildRule(App.getRuleDiffCache().getRuleDiffParts(parts.getPrevDiffId()));
			this.applyDiff(rule, parts.getRuleDiff(), App.getDatabaseConnectionObject().getItemDirectory());
			return rule;
		}
	}
	


	private void transformObjectsInMap(Map<String, Object> map, ItemDirectory itemDirectory) {
		map.computeIfPresent(ActionTypeSpecificKey.Command.getKeyString(), (k, c) -> Command.valueOf(c.toString()));
		map.computeIfPresent(ConditionTypeSpecificKey.Operator.getKeyString(), (k, o) -> Operation.valueOf(o.toString()));
		map.computeIfPresent(TriggerTypeSpecificKey.ItemName.getKeyString(), (k, i) ->  i instanceof SharedItem ? i: itemDirectory.getItemByName(i.toString()));
	}

	public void applyDiff(SharedRule rule, SharedRuleElementDiff diff, ItemDirectory itemDirectory) {
		if (diff.getElementType() instanceof TriggerType) {
			SharedTrigger trigger = getTriggerByRelativeId(rule, diff.getRelativeElementId());
			rule.getTriggers().remove(trigger);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? trigger.getDescription() : diff.getDescription();
			final String label = diff.getLabel() == null ? trigger.getLabel() : diff.getLabel();

			final Map<TriggerTypeSpecificKey, Object> triggerTypeSpecificValues = trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues();

			final Map<String, Object> properties = generalize(triggerTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated(), itemDirectory);

			SharedTrigger newTrigger = new SharedTrigger((TriggerType) diff.getElementType(), properties, description, label, trigger.getRelativeElementId());
			
			rule.getTriggers().add(newTrigger);

		} else if (diff.getElementType() instanceof ConditionType) {
			SharedCondition condition = getConditionByRelativeId(rule, diff.getRelativeElementId());
			rule.getConditions().remove(condition);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? condition.getDescription() : diff.getDescription();
			final String label = diff.getLabel() == null ? condition.getLabel() : diff.getLabel();

			final Map<ConditionTypeSpecificKey, Object> conditionTypeSpecificValues = condition.getConditionTypeContainer().getConditionTypeSpecificValues();
			final Map<String, Object> properties = generalize(conditionTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated(), itemDirectory);

			SharedCondition newCondition = new SharedCondition((ConditionType) diff.getElementType(), properties, description, label, condition.getRelativeElementId());
			rule.getConditions().add(newCondition);
		} else if (diff.getElementType() instanceof ActionType) {
			SharedAction action = getActionByRelativeId(rule, diff.getRelativeElementId());
			rule.getActions().remove(action);
			if (diff.isNegative()) {
				return;
			}
			final String description = diff.getDescription() == null ? action.getDescription() : diff.getDescription();
			final String label = diff.getLabel() == null ? action.getLabel() : diff.getLabel();

			final Map<ActionTypeSpecificKey, Object> actionTypeSpecificValues = action.getActionTypeContainer().getActionTypeSpecificValues();
			final Map<String, Object> properties = generalize(actionTypeSpecificValues);
			updateMap(properties, diff.getPropertiesAdded(), diff.getPropertiesRemoved(), diff.getPropertiesUpdated(), itemDirectory);

			SharedAction newAction = new SharedAction((ActionType) diff.getElementType(), properties, description, label, action.getRelativeElementId());
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

	private void updateMap(Map<String, Object> map, Map<String, Object> toAdd, Map<String, Object> toRemove, Map<String, Object> toUpdate, ItemDirectory itemDirectory) {
		for (Entry<String, Object> entr : toRemove.entrySet()) {
			map.remove(entr.getKey());
		}
		for (Entry<String, Object> entr : toAdd.entrySet()) {
			map.put(entr.getKey(), entr.getValue());
		}
		for (Entry<String, Object> entr : toUpdate.entrySet()) {
			map.put(entr.getKey(), entr.getValue());
		}
		transformObjectsInMap(map, itemDirectory);
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
