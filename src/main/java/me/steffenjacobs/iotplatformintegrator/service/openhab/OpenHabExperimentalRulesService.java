package me.steffenjacobs.iotplatformintegrator.service.openhab;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Configuration;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;

/** @author Steffen Jacobs */
public class OpenHabExperimentalRulesService {

	private final OpenHabSharedService sharedService = new OpenHabSharedService();

	/**
	 * Sends a HTTP GET request to {@link openHabUrlWithPort}/rules to get all
	 * existing rules.
	 * 
	 * @return A list of all found {@link ExperimentalRule}s.
	 */
	public List<ExperimentalRule> requestAllRules(String openHabUrlWithPort) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules"), new TypeReference<List<ExperimentalRule>>() {
		});
	}

	/**
	 * Sends a HTTP POST request to {@link openHabUrlWithPort}/rules to create a new
	 * rule.
	 * 
	 * @return true: if return code was 201 and request was successful.<br/>
	 *         false: else
	 * 
	 */
	public boolean createRule(String openHabUrlWithPort, ExperimentalRule rule) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String createdRule = objectMapper.writeValueAsString(rule);
			System.out.println(createdRule);
			return 201 == sharedService.sendPost(openHabUrlWithPort + "/rest/rules", createdRule);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends a HTTP PUT request to {@link openHabUrlWithPort}/rules/{@link uid} to
	 * update an existing rule with the corresponding {@link uid}.
	 * 
	 * @return true: if return code was 200 and request was successful.<br/>
	 *         false: else
	 * 
	 * @throws IllegalArgumentException
	 *                                      if the uid of the rule does not match
	 *                                      the given uid.
	 * 
	 */
	public boolean updateRuleById(String openHabUrlWithPort, ExperimentalRule rule, String uid) {
		if (!uid.equals(rule.getUid())) {
			throw new IllegalArgumentException("UID of rule (" + rule.getUid() + ") did not match given uid (" + uid + ")!");
		}
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return 200 == sharedService.sendPutWithPathParameters(openHabUrlWithPort + "/rest/rules/" + uid, objectMapper.writeValueAsString(rule));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends a HTTP DELETE request to {@link openHabUrlWithPort}/rules/{@link uid}
	 * to delete the rule with the given uid.
	 * 
	 * @return true: if request was successful.<br/>
	 *         false: else
	 * 
	 */
	public boolean deleteRule(String openHabUrlWithPort, String uid) {
		try {
			return sharedService.sendDelete(openHabUrlWithPort + "/rest/rules/" + uid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends a HTTP GET request to {@link openHabUrlWithPort}/rules/{@link uid} to
	 * get the rule with the corresponding uid.
	 * 
	 * @return the requested rule.
	 * 
	 */
	public ExperimentalRule requestRuleByUid(String openHabUrlWithPort, String uid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules/" + uid), new TypeReference<ExperimentalRule>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends a HTTP GET request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/actions to get all actions
	 * associated with the rule with the given uid.
	 * 
	 * @return the list of actions corresponding to the rule with the given uid.
	 * 
	 */
	public List<Action> getActionsFromRuleById(String openHabUrlWithPort, String uid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules/" + uid + "/actions"), new TypeReference<List<Action>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * Sends a HTTP GET request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/conditions to get all conditions
	 * associated with the rule with the given uid.
	 * 
	 * @return the list of conditions corresponding to the rule with the given uid.
	 * 
	 */
	public List<Condition> getConditionsFromRuleById(String openHabUrlWithPort, String uid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules/" + uid + "/conditions"), new TypeReference<List<Condition>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * Sends a HTTP GET request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/config to get the
	 * {@link Configuration} element associated with the rule with the given uid.
	 * 
	 * @return the configuration corresponding to the rule with the given uid.
	 * 
	 */
	public Configuration getConfigFromRuleById(String openHabUrlWithPort, String uid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules/" + uid + "/config"), new TypeReference<Configuration>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends a HTTP GET request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/triggers to get the
	 * {@link TRIGGER} elements associated with the rule with the given uid.
	 * 
	 * @return the triggers corresponding to the rule with the given uid.
	 * 
	 */
	public List<Trigger> getTriggersFromRuleById(String openHabUrlWithPort, String uid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(new URL(openHabUrlWithPort + "/rest/rules/" + uid + "/triggers"), new TypeReference<List<Trigger>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/**
	 * Sends a HTTP PUT request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/config to update the
	 * {@link Configuration} element associated with the rule with the given uid.
	 * 
	 * @return true: if request was successful.<br/>
	 *         false: else
	 * 
	 */
	public boolean updateConfigurationOfRuleById(String openHabUrlWithPort, String uid, Configuration configuration) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return 200 == sharedService.sendPutWithPathParameters(openHabUrlWithPort + "/rest/rules/" + uid + "/config", objectMapper.writeValueAsString(configuration));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends a HTTP POST request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/runnow to execute the rule with
	 * the given uid immediately.
	 * 
	 * @return true: if request was successful.<br/>
	 *         false: else
	 * 
	 */
	public boolean runRuleByIdNow(String openHabUrlWithPort, String uid) {
		try {
			return 200 == sharedService.sendPost(openHabUrlWithPort + "/rest/rules/" + uid + "/runnow", false, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends a HTTP POST request to
	 * {@link openHabUrlWithPort}/rules/{@link uid}/enable to enable the rule with
	 * the given uid.
	 * 
	 * @return true: if request was successful.<br/>
	 *         false: else
	 * 
	 */
	public boolean enableRuleById(String openHabUrlWithPort, String uid, String body) {
		try {
			return 200 == sharedService.sendPost(openHabUrlWithPort + "/rest/rules/" + uid + "/enable", false, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
