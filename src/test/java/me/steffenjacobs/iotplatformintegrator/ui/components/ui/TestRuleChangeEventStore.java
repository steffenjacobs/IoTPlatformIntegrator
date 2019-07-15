package me.steffenjacobs.iotplatformintegrator.ui.components.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationServiceMock;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleChangeEventStore;

public class TestRuleChangeEventStore {

	@Test
	public void testDiffApplicationForTriggerWithChangedDescription() {
		SharedRule rule = createSharedRule();

		SharedRuleElementDiff diff = new SharedRuleElementDiff("NewItemStateChangedDescription", null,
				TriggerType.CommandReceived, null, null, null, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore(new AuthenticationServiceMock());

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertEquals(1, rule.getActions().size());
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedTrigger newTrigger = rule.getTriggers().iterator().next();
		Assert.assertEquals("NewItemStateChangedDescription", newTrigger.getDescription());
		Assert.assertEquals("ItemStateChangedLabel", newTrigger.getLabel());
		Assert.assertEquals(1, newTrigger.getRelativeElementId());
		Assert.assertEquals(TriggerType.CommandReceived, newTrigger.getTriggerTypeContainer().getTriggerType());
		Assert.assertEquals(2, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().size());
		Assert.assertEquals(Command.On, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues()
				.get(TriggerTypeSpecificKey.Command));
		Assert.assertEquals(createTestItem("TestSwitch"), newTrigger.getTriggerTypeContainer()
				.getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
	}

	@Test
	public void testDiffApplicationForTriggerWithChangedProperty() {
		SharedRule rule = createSharedRule();

		Map<String, Object> propertiesUpdated = new HashMap<>();
		propertiesUpdated.put(TriggerTypeSpecificKey.Command.getKeyString(), Command.Off);
		SharedRuleElementDiff diff = new SharedRuleElementDiff(null, null, TriggerType.CommandReceived, null, null,
				propertiesUpdated, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore(new AuthenticationServiceMock());

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertEquals(1, rule.getActions().size());
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedTrigger newTrigger = rule.getTriggers().iterator().next();
		Assert.assertEquals("ItemStateChangedDescription", newTrigger.getDescription());
		Assert.assertEquals("ItemStateChangedLabel", newTrigger.getLabel());
		Assert.assertEquals(1, newTrigger.getRelativeElementId());
		Assert.assertEquals(TriggerType.CommandReceived, newTrigger.getTriggerTypeContainer().getTriggerType());
		Assert.assertEquals(2, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().size());
		Assert.assertEquals(Command.Off, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues()
				.get(TriggerTypeSpecificKey.Command));
		Assert.assertEquals(createTestItem("TestSwitch"), newTrigger.getTriggerTypeContainer()
				.getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
	}

	private SharedItem createTestItem(String name) {
		return new SharedItem(name, "TestLabel", ItemType.Switch);
	}

	@Test
	public void testDiffApplicationForTriggerWithAddedRemovedAndChangedProperty() {
		SharedRule rule = createSharedRule();

		Map<String, Object> propertiesCreated = new HashMap<>();
		propertiesCreated.put(TriggerTypeSpecificKey.State.getKeyString(), Command.Off);

		Map<String, Object> propertiesRemoved = new HashMap<>();
		propertiesRemoved.put(TriggerTypeSpecificKey.Command.getKeyString(), null);

		Map<String, Object> propertiesUpdated = new HashMap<>();
		propertiesUpdated.put(TriggerTypeSpecificKey.ItemName.getKeyString(), createTestItem("TestSwitch2"));

		SharedRuleElementDiff diff = new SharedRuleElementDiff(null, null, TriggerType.ItemStateUpdated,
				propertiesCreated, propertiesRemoved, propertiesUpdated, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore(new AuthenticationServiceMock());

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertEquals(1, rule.getActions().size());
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedTrigger newTrigger = rule.getTriggers().iterator().next();
		Assert.assertEquals("ItemStateChangedDescription", newTrigger.getDescription());
		Assert.assertEquals("ItemStateChangedLabel", newTrigger.getLabel());
		Assert.assertEquals(1, newTrigger.getRelativeElementId());
		Assert.assertEquals(TriggerType.ItemStateUpdated, newTrigger.getTriggerTypeContainer().getTriggerType());
		Assert.assertEquals(2, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().size());
		Assert.assertEquals(Command.Off,
				newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.State));
		Assert.assertEquals(createTestItem("TestSwitch2"), newTrigger.getTriggerTypeContainer()
				.getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
	}

	@Test
	public void testDiffApplicationForConditionWithAddedRemovedAndChangedProperty() {
		SharedRule rule = createSharedRule();

		Map<String, Object> propertiesCreated = new HashMap<>();
		propertiesCreated.put(ConditionTypeSpecificKey.StartTime.getKeyString(), "12:34:56");
		propertiesCreated.put(ConditionTypeSpecificKey.EndTime.getKeyString(), "13:34:56");

		Map<String, Object> propertiesRemoved = new HashMap<>();
		propertiesRemoved.put(ConditionTypeSpecificKey.Operator.getKeyString(), null);
		propertiesRemoved.put(ConditionTypeSpecificKey.ItemName.getKeyString(), null);

		Map<String, Object> propertiesUpdated = new HashMap<>();

		SharedRuleElementDiff diff = new SharedRuleElementDiff(null, null, ConditionType.TimeOfDay, propertiesCreated,
				propertiesRemoved, propertiesUpdated, false, 1337);
		RuleChangeEventStore store = new RuleChangeEventStore(new AuthenticationServiceMock());

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertEquals(1, rule.getActions().size());
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedCondition newCondition = rule.getConditions().iterator().next();
		Assert.assertEquals("ConditionItemStateDescription", newCondition.getDescription());
		Assert.assertEquals("ConditionItemStateLabel", newCondition.getLabel());
		Assert.assertEquals(1337, newCondition.getRelativeElementId());
		Assert.assertEquals(ConditionType.TimeOfDay, newCondition.getConditionTypeContainer().getConditionType());
		Assert.assertEquals(2, newCondition.getConditionTypeContainer().getConditionTypeSpecificValues().size());
		Assert.assertEquals("12:34:56", newCondition.getConditionTypeContainer().getConditionTypeSpecificValues()
				.get(ConditionTypeSpecificKey.StartTime));
		Assert.assertEquals("13:34:56", newCondition.getConditionTypeContainer().getConditionTypeSpecificValues()
				.get(ConditionTypeSpecificKey.EndTime));
	}

	@Test
	public void testDiffApplicationForActionWithAddedRemovedAndChangedProperty() {
		SharedRule rule = createSharedRule();

		Map<String, Object> propertiesCreated = new HashMap<>();
		propertiesCreated.put(ActionTypeSpecificKey.Text.getKeyString(), "Hello World");
		propertiesCreated.put(ActionTypeSpecificKey.Sink.getKeyString(), "javasound");

		Map<String, Object> propertiesRemoved = new HashMap<>();
		propertiesRemoved.put(ActionTypeSpecificKey.ItemName.getKeyString(), null);
		propertiesRemoved.put(ActionTypeSpecificKey.Command.getKeyString(), null);

		Map<String, Object> propertiesUpdated = new HashMap<>();

		SharedRuleElementDiff diff = new SharedRuleElementDiff(null, null, ActionType.SaySomething, propertiesCreated,
				propertiesRemoved, propertiesUpdated, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore(new AuthenticationServiceMock());

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertEquals(1, rule.getActions().size());
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedAction newAction = rule.getActions().iterator().next();
		Assert.assertEquals("ItemCommandActionDescription", newAction.getDescription());
		Assert.assertEquals("ItemCommandActionLabel", newAction.getLabel());
		Assert.assertEquals(1, newAction.getRelativeElementId());
		Assert.assertEquals(ActionType.SaySomething, newAction.getActionTypeContainer().getActionType());
		Assert.assertEquals(2, newAction.getActionTypeContainer().getActionTypeSpecificValues().size());
		Assert.assertEquals("Hello World",
				newAction.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Text));
		Assert.assertEquals("javasound",
				newAction.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.Sink));
	}

	private SharedRule createSharedRule() {
		Set<SharedTrigger> triggers = new HashSet<>();
		Set<SharedCondition> conditions = new HashSet<>();
		Set<SharedAction> actions = new HashSet<>();

		Map<String, Object> properties = new HashMap<>();
		properties.put(TriggerTypeSpecificKey.Command.getKeyString(), Command.On);
		properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), createTestItem("TestSwitch"));

		Map<String, Object> properties2 = new HashMap<>();
		properties2.put(ConditionTypeSpecificKey.ItemName.getKeyString(), createTestItem("TestSwitch"));
		properties2.put(ConditionTypeSpecificKey.Operator.getKeyString(), Operation.EQUAL);
		properties2.put(ConditionTypeSpecificKey.State.getKeyString(), Command.Off);

		Map<String, Object> properties3 = new HashMap<>();
		properties3.put(ActionTypeSpecificKey.ItemName.getKeyString(), createTestItem("TestSwitch"));
		properties2.put(ActionTypeSpecificKey.Command.getKeyString(), Command.Off);

		SharedCondition conditionToTest = new SharedCondition(ConditionType.ItemState, properties2,
				"ConditionItemStateDescription", "ConditionItemStateLabel", 1337);
		conditions.add(conditionToTest);

		SharedTrigger triggerToTest = new SharedTrigger(TriggerType.CommandReceived, properties,
				"ItemStateChangedDescription", "ItemStateChangedLabel", 1);
		triggers.add(triggerToTest);

		SharedAction actionToTest = new SharedAction(ActionType.ItemCommand, properties3,
				"ItemCommandActionDescription", "ItemCommandActionLabel", 1);
		actions.add(actionToTest);

		SharedRule rule = new SharedRule("TestRule", "TestRule.id", "Test Description", "false", "ACTIVE", triggers,
				conditions, actions);
		return rule;
	}

}
