package me.steffenjacobs.iotplatformintegrator.ui.components.ui;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.DiffResult;
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
		Assert.assertFalse(diffSharedRuleElement.isNegative());
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
		Assert.assertFalse(diffSharedRuleElement.isNegative());
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
		Assert.assertEquals(1, diffSharedRuleElement.getPropertiesAdded().size());
		Assert.assertEquals(Command.Off, diffSharedRuleElement.getPropertiesAdded().get(TriggerTypeSpecificKey.PreviousState.getKeyString()));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
		Assert.assertFalse(diffSharedRuleElement.isNegative());
	}

	@Test
	public void testRemove() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);
		final Map<String, Object> properties2 = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateChanged);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateChanged, properties2, "Hello World", "Some Label");
		SharedTrigger sharedTrigger2 = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, sharedTrigger2);

		Assert.assertNull(diffSharedRuleElement.getLabel());
		Assert.assertNull(diffSharedRuleElement.getDescription());
		Assert.assertNotNull(diffSharedRuleElement.getElementType());
		Assert.assertEquals(TriggerType.ItemStateUpdated, diffSharedRuleElement.getElementType());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertEquals(1, diffSharedRuleElement.getPropertiesRemoved().size());
		Assert.assertEquals(Command.Off, diffSharedRuleElement.getPropertiesRemoved().get(TriggerTypeSpecificKey.PreviousState.getKeyString()));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
		Assert.assertFalse(diffSharedRuleElement.isNegative());
	}

	@Test
	public void testUpdate() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);
		final Map<String, Object> properties2 = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);

		final SharedItem item2 = new SharedItem("testSwitch2", "testLabel", ItemType.Switch);
		properties2.put(TriggerTypeSpecificKey.ItemName.getKeyString(), item2);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");
		SharedTrigger sharedTrigger2 = new SharedTrigger(TriggerType.ItemStateUpdated, properties2, "Hello World", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, sharedTrigger2);

		Assert.assertNull(diffSharedRuleElement.getLabel());
		Assert.assertNull(diffSharedRuleElement.getDescription());
		Assert.assertNull(diffSharedRuleElement.getElementType());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertEquals(1, diffSharedRuleElement.getPropertiesUpdated().size());
		Assert.assertEquals(item2, diffSharedRuleElement.getPropertiesUpdated().get(TriggerTypeSpecificKey.ItemName.getKeyString()));
		Assert.assertFalse(diffSharedRuleElement.isNegative());
	}

	@Test
	public void testAddedElementDiff() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(null, sharedTrigger);

		Assert.assertNotNull(diffSharedRuleElement.getLabel());
		Assert.assertEquals("Some Label", diffSharedRuleElement.getLabel());
		Assert.assertNotNull(diffSharedRuleElement.getDescription());
		Assert.assertEquals("Hello World", diffSharedRuleElement.getDescription());
		Assert.assertNotNull(diffSharedRuleElement.getElementType());
		Assert.assertEquals(TriggerType.ItemStateUpdated, diffSharedRuleElement.getElementType());
		Assert.assertEquals(2, diffSharedRuleElement.getPropertiesAdded().size());
		Assert.assertTrue(
				diffSharedRuleElement.getPropertiesAdded().get(TriggerTypeSpecificKey.ItemName.getKeyString()).equals(new SharedItem("testSwitch", "testLabel", ItemType.Switch)));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().get(TriggerTypeSpecificKey.State.getKeyString()).equals(Command.On));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
		Assert.assertFalse(diffSharedRuleElement.isNegative());
	}

	@Test
	public void testRemovedElementDiff() {
		RuleDiffService diffService = new RuleDiffService();

		final Map<String, Object> properties = createPropertiesForItemStateUpdatedExample(TriggerType.ItemStateUpdated);

		SharedTrigger sharedTrigger = new SharedTrigger(TriggerType.ItemStateUpdated, properties, "Hello World", "Some Label");

		SharedRuleElementDiff diffSharedRuleElement = diffService.getDiffSharedRuleElement(sharedTrigger, null);

		Assert.assertNotNull(diffSharedRuleElement.getLabel());
		Assert.assertEquals("Some Label", diffSharedRuleElement.getLabel());
		Assert.assertNotNull(diffSharedRuleElement.getDescription());
		Assert.assertEquals("Hello World", diffSharedRuleElement.getDescription());
		Assert.assertNotNull(diffSharedRuleElement.getElementType());
		Assert.assertEquals(TriggerType.ItemStateUpdated, diffSharedRuleElement.getElementType());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesAdded().isEmpty());
		Assert.assertEquals(2, diffSharedRuleElement.getPropertiesRemoved().size());
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().get(TriggerTypeSpecificKey.ItemName.getKeyString())
				.equals(new SharedItem("testSwitch", "testLabel", ItemType.Switch)));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesRemoved().get(TriggerTypeSpecificKey.State.getKeyString()).equals(Command.On));
		Assert.assertTrue(diffSharedRuleElement.getPropertiesUpdated().isEmpty());
		Assert.assertTrue(diffSharedRuleElement.isNegative());
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
