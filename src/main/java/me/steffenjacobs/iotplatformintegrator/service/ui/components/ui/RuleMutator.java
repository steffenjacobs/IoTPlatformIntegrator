package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.swing.JOptionPane;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCopiedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCreatedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementDeletedEvent;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;

/** @author Steffen Jacobs */
public class RuleMutator {

	private final RuleBuilderRenderController ruleBuilderController;

	public RuleMutator(RuleBuilderRenderController ruleBuilderController, SettingService settingService) {
		this.ruleBuilderController = ruleBuilderController;
		final boolean enableEvaluationFeatures = !"1".equals(settingService.getSetting(SettingKey.DISABLE_EVALUATION_FEATURES));
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_CREATED,
				e -> createRuleElement(((RuleElementCreatedEvent) e).getElementType(), ((RuleElementCreatedEvent) e).getSharedElementType()));
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_COPIED, e -> {
			if (enableEvaluationFeatures)
				copyRuleElement(((RuleElementCopiedEvent) e).getSourceId());
			else
				showError();
		});
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_DELETED, e -> deleteRuleElement(((RuleElementDeletedEvent) e).getSourceId()));
	}

	private void showError() {
		JOptionPane.showMessageDialog(null, "This feature is disabled during your evaluation.", "Feature Disabled", JOptionPane.INFORMATION_MESSAGE);
	}

	private void createRuleElement(ElementType elementType, SharedElementType sharedElementType) {
		Optional<SharedRule> oRule = ruleBuilderController.getDisplayedRule();
		oRule.ifPresent(selectedRule -> {
			switch (elementType) {
			case TRIGGER:
				int relativeElementId = findHighestId(selectedRule) + 1;
				final SharedTrigger trigger = new SharedTrigger((TriggerType) sharedElementType, createDefaults(sharedElementType), "TRIGGER-" + relativeElementId,
						sharedElementType.name() + " (" + relativeElementId + ")", relativeElementId);
				selectedRule.getTriggers().add(trigger);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, trigger, null));
				break;
			case CONDITION:
				int relativeElementId2 = findHighestId(selectedRule) + 1;
				final SharedCondition condition = new SharedCondition((ConditionType) sharedElementType, createDefaults(sharedElementType), "CONDITION-" + relativeElementId2,
						sharedElementType.name() + " (" + relativeElementId2 + ")", relativeElementId2);
				selectedRule.getConditions().add(condition);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, condition, null));
				break;
			case ACTION:
				int relativeElementId3 = findHighestId(selectedRule) + 1;
				final SharedAction action = new SharedAction((ActionType) sharedElementType, createDefaults(sharedElementType), "ACTION-" + relativeElementId3,
						sharedElementType.name() + " (" + relativeElementId3 + ")", relativeElementId3);
				selectedRule.getActions().add(action);
				EventBus.getInstance().fireEvent(new RuleChangeEvent(selectedRule, action, null));
				break;
			}
		});
	}

	private Map<String, Object> createDefaults(SharedElementType type) {
		final Map<String, Object> map = new HashMap<>();
		if (type instanceof TriggerType) {
			for (TriggerTypeSpecificKey key : ((TriggerType) type).getTypeSpecificKeys()) {
				map.put(key.getKeyString(), "");
			}
		} else if (type instanceof ConditionType) {
			for (ConditionTypeSpecificKey key : ((ConditionType) type).getTypeSpecificKeys()) {
				map.put(key.getKeyString(), "");
			}
		} else if (type instanceof ActionType) {
			for (ActionTypeSpecificKey key : ((ActionType) type).getTypeSpecificKeys()) {
				map.put(key.getKeyString(), "");
			}
		}

		final ItemDirectory dummyCreator = new ItemDirectory();
		map.computeIfPresent(TriggerTypeSpecificKey.Command.getKeyString(), (k, v) -> Command.Off);
		map.computeIfPresent(TriggerTypeSpecificKey.PreviousState.getKeyString(), (k, v) -> 0);
		map.computeIfPresent(TriggerTypeSpecificKey.State.getKeyString(), (k, v) -> 0);
		map.computeIfPresent(TriggerTypeSpecificKey.Channel.getKeyString(), (k, v) -> "channel-0");
		map.computeIfPresent(TriggerTypeSpecificKey.Event.getKeyString(), (k, v) -> "");
		map.computeIfPresent(TriggerTypeSpecificKey.EventData.getKeyString(), (k, v) -> "");
		map.computeIfPresent(TriggerTypeSpecificKey.ItemName.getKeyString(), (k, v) -> dummyCreator.getItemByName(null));
		map.computeIfPresent(TriggerTypeSpecificKey.Time.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ConditionTypeSpecificKey.Script.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ConditionTypeSpecificKey.Type.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ConditionTypeSpecificKey.Operator.getKeyString(), (k, v) -> Operation.BIGGER_EQUAL);
		map.computeIfPresent(ConditionTypeSpecificKey.StartTime.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ConditionTypeSpecificKey.EndTime.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ActionTypeSpecificKey.Enable.getKeyString(), (k, v) -> false);
		map.computeIfPresent(ActionTypeSpecificKey.RuleUUIDs.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ActionTypeSpecificKey.Sink.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ActionTypeSpecificKey.Sound.getKeyString(), (k, v) -> "");
		map.computeIfPresent(ActionTypeSpecificKey.ConsiderConditions.getKeyString(), (k, v) -> false);
		return map;
	}

	private int findHighestId(SharedRule rule) {
		int highest = 0;

		final Set<SharedRuleElement> elems = new HashSet<>();
		elems.addAll(rule.getTriggers());
		elems.addAll(rule.getConditions());
		elems.addAll(rule.getActions());

		for (SharedRuleElement t : elems) {
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
				rule.getTriggers().add(copy((SharedTrigger) elem, findHighestId(rule) + 1));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			} else if (elem instanceof SharedCondition) {
				rule.getConditions().add(copy((SharedCondition) elem, findHighestId(rule) + 1));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			} else if (elem instanceof SharedAction) {
				rule.getActions().add(copy((SharedAction) elem, findHighestId(rule) + 1));
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, elem, null));
			}
		}
	}

	private void deleteRuleElement(UUID sourceId) {
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

	private SharedTrigger copy(SharedTrigger trigger, int newElementId) {
		Map<String, Object> properties = new HashMap<>();
		trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedTrigger(trigger.getTriggerTypeContainer().getTriggerType(), properties, trigger.getDescription(), trigger.getLabel() + " - Copy", newElementId);
	}

	private SharedCondition copy(SharedCondition condition, int newElementId) {
		Map<String, Object> properties = new HashMap<>();
		condition.getConditionTypeContainer().getConditionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedCondition(condition.getConditionTypeContainer().getConditionType(), properties, condition.getDescription(), condition.getLabel() + " - Copy",
				newElementId);
	}

	private SharedAction copy(SharedAction action, int newElementId) {
		Map<String, Object> properties = new HashMap<>();
		action.getActionTypeContainer().getActionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedAction(action.getActionTypeContainer().getActionType(), properties, action.getDescription(), action.getLabel() + " - Copy", newElementId);
	}
}
