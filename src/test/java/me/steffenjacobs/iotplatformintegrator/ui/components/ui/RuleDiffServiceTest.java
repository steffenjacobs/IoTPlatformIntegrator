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

		for (TriggerTypeSpecificKey key : TriggerType.ItemStateUpdated.getTypeSpecificKeys()) {
			if (key != TriggerTypeSpecificKey.ItemName && key != TriggerTypeSpecificKey.State) {
				fail("Unexpected TriggerTypeSpecific key: " + key.name());
			}
		}

		final Map<String, Object> properties = new HashMap<>();

		SharedItem testSwitch = new SharedItem("testSwitch", "testLabel", ItemType.Switch);
		properties.put(TriggerTypeSpecificKey.ItemName.getKeyString(), testSwitch);
		properties.put(TriggerTypeSpecificKey.State.getKeyString(), Command.On);

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

}
