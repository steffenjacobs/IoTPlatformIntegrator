package me.steffenjacobs.iotplatformintegrator.ui.components.ui;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleDiffService;

/** @author Steffen Jacobs */
public class RuleDiffServiceTest {

	@Test
	public void testDescriptionChange() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");
		SharedTrigger sharedTrigger2 = new SharedTrigger(TriggerType.ItemStateUpdated, new HashMap<>(properties), "Hello World2", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, sharedTrigger2);

		Assert.assertNull(diffSharedRuleElement.getLabel());
		Assert.assertNotNull(diffSharedRuleElement.getDescription());
		Assert.assertEquals("Hello World2", diffSharedRuleElement.getDescription());
		Assert.assertNull(diffSharedRuleElement.getElementType());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
	}

	@Test
	public void testLabelChange() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");
		SharedTrigger sharedTrigger2 = new SharedTrigger(TriggerType.ItemStateUpdated, new HashMap<>(properties), "Hello World", "Some Label2");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, sharedTrigger2);

		Assert.assertNotNull(diffSharedRuleElement.getLabel());
		Assert.assertEquals("Some Label2", diffSharedRuleElement.getLabel());
		Assert.assertNull(diffSharedRuleElement.getDescription());
		Assert.assertNull(diffSharedRuleElement.getElementType());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
	}

	@Test
	public void testAdd() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);
		final Map<String, Object> properties2 = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateChanged);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");
		SharedTrigger sharedTrigger2 = new SharedTrigger(TriggerType.ItemStateChanged, properties2, "Hello World", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, sharedTrigger2);

		Assert.assertNull(diffSharedRuleElement.getLabel());
		Assert.assertNull(diffSharedRuleElement.getDescription());
		Assert.assertNotNull(diffSharedRuleElement.getElementType());
		Assert.assertEquals(TriggerType.ItemStateChanged, diffSharedRuleElement.getElementType());
		Assert.assertFalse(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().size() == 1);
		Assert.assertEquals(Command.Off, diffSharedRuleElement.getPropertiesAdded().get(TriggerTypeSpecificKey.PreviousState.getKeyString()));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
	}

	private Map<String, Object> createPropertiesForItemStateUpdatedExample(TriggerType type) {
		final Map<String, Object> properties = new HashMap<>();
		final SharedItem testSwitch = new SharedItem("testSwitch", "testLabel", ItemType.Switch);
		if (type == TriggerType.ItemStateUpdated) {
			for (TriggerTypeSpecificKey key : type.getTypeSpecificKeys()) {
				if (key != TriggerTypeSpecificKey.ItemName && key != TriggerTypeSpecificKey.State) {
					fail("Unexpected TriggerTypeSpecific key: " + key.name());
				}
			}

			properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), testSwitch);
			properties.put(TriggerTypeSpecificKey.State.getKeyString(), Command.On);
		} else if (type == TriggerType.ItemStateChanged) {
			for (TriggerTypeSpecificKey key : type.getTypeSpecificKeys()) {
				if (key != TriggerTypeSpecificKey.ItemName && key != TriggerTypeSpecificKey.State && key != TriggerTypeSpecificKey.PreviousState) {
					fail("Unexpected TriggerTypeSpecific key: " + key.name());
				}
			}

			properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), testSwitch);
			properties.put(TriggerTypeSpecificKey.State.getKeyString(), Command.On);
			properties.put(TriggerTypeSpecificKey.PreviousState.getKeyString(), Command.Off);
		}
		return properties;
	}

}
