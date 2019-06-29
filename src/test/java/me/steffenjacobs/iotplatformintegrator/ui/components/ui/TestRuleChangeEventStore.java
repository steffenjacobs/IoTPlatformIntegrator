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
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleChangeEventStore;

public class TestRuleChangeEventStore {

	@Test
	public void testDiffApplicationForTriggerWithChangedDescription() {
		SharedRule rule = createSharedRule();

		SharedRuleElementDiff diff = new SharedRuleElementDiff("NewItemStateChangedDescription", null,
				TriggerType.CommandReceived, null, null, null, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore();

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertTrue(rule.getActions().isEmpty());
		Assert.assertTrue(rule.getConditions().isEmpty());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedTrigger newTrigger = rule.getTriggers().iterator().next();
		Assert.assertEquals("NewItemStateChangedDescription", newTrigger.getDescription());
		Assert.assertEquals("ItemStateChangedLabel", newTrigger.getLabel());
		Assert.assertEquals(1, newTrigger.getRelativeElementId());
		Assert.assertEquals(TriggerType.CommandReceived, newTrigger.getTriggerTypeContainer().getTriggerType());
		Assert.assertEquals(2, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().size());
		Assert.assertEquals(Command.On, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues()
				.get(TriggerTypeSpecificKey.Command));
		Assert.assertEquals(new SharedItem("TestSwitch", "TestLabel", ItemType.Switch), newTrigger
				.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
	}

	@Test
	public void testDiffApplicationForTriggerWithChangedProperty() {
		SharedRule rule = createSharedRule();

		Map<String, Object> propertiesUpdated = new HashMap<>();
		propertiesUpdated.put(TriggerTypeSpecificKey.Command.getKeyString(), Command.Off);
		SharedRuleElementDiff diff = new SharedRuleElementDiff(null, null, TriggerType.CommandReceived, null, null,
				propertiesUpdated, false, 1);
		RuleChangeEventStore store = new RuleChangeEventStore();

		store.applyDiff(rule, diff);

		Assert.assertEquals("Test Description", rule.getDescription());
		Assert.assertEquals("TestRule", rule.getName());
		Assert.assertEquals("TestRule.id", rule.getId());
		Assert.assertEquals("false", rule.getVisible());
		Assert.assertEquals("ACTIVE", rule.getStatus());

		Assert.assertTrue(rule.getActions().isEmpty());
		Assert.assertTrue(rule.getConditions().isEmpty());
		Assert.assertEquals(1, rule.getTriggers().size());
		SharedTrigger newTrigger = rule.getTriggers().iterator().next();
		Assert.assertEquals("ItemStateChangedDescription", newTrigger.getDescription());
		Assert.assertEquals("ItemStateChangedLabel", newTrigger.getLabel());
		Assert.assertEquals(1, newTrigger.getRelativeElementId());
		Assert.assertEquals(TriggerType.CommandReceived, newTrigger.getTriggerTypeContainer().getTriggerType());
		Assert.assertEquals(2, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().size());
		Assert.assertEquals(Command.Off, newTrigger.getTriggerTypeContainer().getTriggerTypeSpecificValues()
				.get(TriggerTypeSpecificKey.Command));
		Assert.assertEquals(new SharedItem("TestSwitch", "TestLabel", ItemType.Switch), newTrigger
				.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName));
	}

	private SharedRule createSharedRule() {
		Set<SharedTrigger> triggers = new HashSet<>();
		Set<SharedCondition> conditions = new HashSet<>();
		Set<SharedAction> actions = new HashSet<>();

		Map<String, Object> properties = new HashMap<>();
		properties.put(TriggerTypeSpecificKey.Command.getKeyString(), Command.On);
		properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(),
				new SharedItem("TestSwitch", "TestLabel", ItemType.Switch));

		SharedTrigger triggerToTest = new SharedTrigger(TriggerType.CommandReceived, properties,
				"ItemStateChangedDescription", "ItemStateChangedLabel", 1);
		triggers.add(triggerToTest);

		SharedRule rule = new SharedRule("TestRule", "TestRule.id", "Test Description", "false", "ACTIVE", triggers,
				conditions, actions);
		return rule;
	}

}
