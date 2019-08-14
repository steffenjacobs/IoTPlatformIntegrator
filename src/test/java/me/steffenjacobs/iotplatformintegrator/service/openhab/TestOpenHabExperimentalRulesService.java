package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ConfigDescription;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Configuration;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Inputs;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Status;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;

/** @author Steffen Jacobs */
public class TestOpenHabExperimentalRulesService {
	private static final String OH_URL_WITH_PORT = "http://localhost:8080";

	@Test
	public void testExperimentalRulesService() throws IOException {

		// get all rules -> should be zero
		List<ExperimentalRule> list = new OpenHabExperimentalRulesService().requestAllRules(OH_URL_WITH_PORT);
		final int initialRuleCount = list.size();

		ExperimentalRule rule = createTestRule();

		// create new rule
		boolean create = new OpenHabExperimentalRulesService().createRule(OH_URL_WITH_PORT, rule);
		Assert.assertTrue(create);

		// retrieve created rule
		ExperimentalRule ruleRetrieved = new OpenHabExperimentalRulesService().requestRuleByUid(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertNotNull(ruleRetrieved);
		Assert.assertEquals(rule.getName(), ruleRetrieved.getName());

		// retrieve all rules again -> should be one
		list = new OpenHabExperimentalRulesService().requestAllRules(OH_URL_WITH_PORT);
		Assert.assertEquals(initialRuleCount + 1, list.size());

		// get actions
		List<Action> actions = new OpenHabExperimentalRulesService().getActionsFromRuleById(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertEquals(1, actions.size());
		Action action = actions.get(0);
		Assert.assertEquals("2", action.getId());
		Assert.assertEquals("send a command", action.getLabel());
		Assert.assertEquals("Sends a command to a specified item.", action.getDescription());

		// get conditions
		List<Condition> conditions = new OpenHabExperimentalRulesService().getConditionsFromRuleById(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertEquals(0, conditions.size());

		// get config
		Configuration config = new OpenHabExperimentalRulesService().getConfigFromRuleById(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertNotNull(config);
		Assert.assertEquals("testvalue", config.getAdditionalProperties().get("test"));

		// get triggers
		List<Trigger> triggers = new OpenHabExperimentalRulesService().getTriggersFromRuleById(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertEquals(1, actions.size());
		Trigger t = triggers.get(0);
		Assert.assertEquals("1", t.getId());
		Assert.assertEquals("core.ItemStateChangeTrigger", t.getType());
		Assert.assertEquals("HS110_EnergyUsage", t.getConfiguration().getAdditionalProperties().get("itemName"));

		// run rule
		boolean run = new OpenHabExperimentalRulesService().runRuleByIdNow(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertTrue(run);

		// enable rule
		boolean enable = new OpenHabExperimentalRulesService().enableRuleById(OH_URL_WITH_PORT, rule.getUid(), "enable");
		Assert.assertTrue(enable);

		// update configuration
		Configuration configNew = new Configuration();
		configNew.getAdditionalProperties().put("test2", "testvalue2");
		boolean updateConfig = new OpenHabExperimentalRulesService().updateConfigurationOfRuleById(OH_URL_WITH_PORT, rule.getUid(), configNew);
		Assert.assertTrue(updateConfig);

		// check updated config
		config = new OpenHabExperimentalRulesService().getConfigFromRuleById(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertNotNull(config);
		Assert.assertEquals("testvalue2", config.getAdditionalProperties().get("test2"));

		// update rule
		ExperimentalRule rule2 = createTestRule();
		rule2.setName("TestRule");

		boolean update = new OpenHabExperimentalRulesService().updateRuleById(OH_URL_WITH_PORT, rule2, "9c2f6e40-4b21-490d-bb55-9d6f1e97d106");
		Assert.assertTrue(update);

		ExperimentalRule ruleRetrieved2 = new OpenHabExperimentalRulesService().requestRuleByUid(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertNotNull(ruleRetrieved2);
		Assert.assertEquals(rule2.getName(), ruleRetrieved2.getName());

		// delete created rule
		boolean delete = new OpenHabExperimentalRulesService().deleteRule(OH_URL_WITH_PORT, rule.getUid());
		Assert.assertTrue(delete);

		// retrieve all rules again -> should be zero again
		list = new OpenHabExperimentalRulesService().requestAllRules(OH_URL_WITH_PORT);
		Assert.assertEquals(initialRuleCount, list.size());
	}

	private ExperimentalRule createTestRule() {
		ExperimentalRule rule = new ExperimentalRule();
		Status status = new Status();
		status.setStatus("IDLE");
		status.setStatusDetail("NONE");
		rule.setStatus(status);

		Trigger trigger = new Trigger();
		trigger.setId("1");
		trigger.setLabel("an item state changes");
		trigger.setDescription("This triggers the rule if an item state has changed.");

		Configuration configuration = new Configuration();
		configuration.setAdditionalProperty("itemName", "HS110_EnergyUsage");
		trigger.setConfiguration(configuration);
		trigger.setType("core.ItemStateChangeTrigger");

		rule.setTriggers(Arrays.asList(trigger));

		rule.setConditions(new ArrayList<Condition>());

		Action action = new Action();
		action.setInputs(new Inputs());
		action.setId("2");
		action.setLabel("send a command");
		action.setDescription("Sends a command to a specified item.");

		Configuration configuration2 = new Configuration();
		configuration2.setAdditionalProperty("itemName", "HS110_Switch");
		configuration2.setAdditionalProperty("command", "OFF");
		action.setConfiguration(configuration2);
		action.setType("core.ItemCommandAction");

		rule.setActions(Arrays.asList(action));

		Configuration configuration3 = new Configuration();
		configuration3.setAdditionalProperty("test", "testvalue");
		rule.setConfiguration(configuration3);
		rule.setConfigDescriptions(new ArrayList<ConfigDescription>());
		rule.setUid("9c2f6e40-4b21-490d-bb55-9d6f1e97d106");
		rule.setName("TurnOffOnLowPower");
		rule.setTags(new ArrayList<String>());
		rule.setVisibility("VISIBLE");
		return rule;
	}
}
