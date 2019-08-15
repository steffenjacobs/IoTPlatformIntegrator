package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Action;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Configuration;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Status;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformRuleReverseTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabRuleReverseTransformationAdapter implements PlatformRuleReverseTransformationAdapter<ExperimentalRule> {
	private static final Logger LOG = LoggerFactory.getLogger(OpenHabRuleReverseTransformationAdapter.class);

	private final OpenHabConditionReverseTransformationAdapter conditionReverseTransformer;
	private final OpenHabTriggerReverseTransformationAdapter triggerReverseTransformer;
	private final OpenHabActionReverseTransformationAdapter actionReverseTransformer;

	public OpenHabRuleReverseTransformationAdapter() {
		conditionReverseTransformer = new OpenHabConditionReverseTransformationAdapter();
		triggerReverseTransformer = new OpenHabTriggerReverseTransformationAdapter();
		actionReverseTransformer = new OpenHabActionReverseTransformationAdapter();
	}

	@Override
	public ExperimentalRule transformRule(SharedRule rule, OpenHabCommandReverseTransformer reverseCommandParser) {
		// create defensive copy
		rule = new SharedRule(rule);

		// convert master data
		final ExperimentalRule result = new ExperimentalRule();
		result.setName(rule.getName());
		result.setUid(rule.getId());
		result.setDescription(rule.getDescription());
		result.setStatus(getStatus(rule));
		result.setVisibility(rule.getVisible());

		// conditions
		final List<Condition> conditions = new ArrayList<>();
		for (SharedCondition sc : rule.getConditions()) {
			conditions.add(reverseTransformCondition(sc));
		}
		result.setConditions(conditions);

		// triggers
		final List<Trigger> triggers = new ArrayList<>();
		for (SharedTrigger st : rule.getTriggers()) {
			triggers.add(reverseTransformTrigger(st, reverseCommandParser));
		}
		result.setTriggers(triggers);

		// actions
		final List<Action> actions = new ArrayList<>();
		for (SharedAction sa : rule.getActions()) {
			actions.add(reverseTransformAction(sa, reverseCommandParser));
		}
		result.setActions(actions);

		return result;
	}

	private Condition reverseTransformCondition(SharedCondition sc) {
		final Condition result = new Condition();
		result.setDescription(sc.getDescription());
		result.setType(getReverseConditionType(sc.getConditionTypeContainer().getConditionType()));
		result.setLabel(sc.getLabel());

		// configuration
		conditionReverseTransformer.convertConditionTypeContainer(sc.getConditionTypeContainer());
		final Configuration configuration = new Configuration();
		sc.getConditionTypeContainer().getConditionTypeSpecificValues().forEach((k, v) -> {
			configuration.setAdditionalProperty(k.getKeyString(), v);
		});
		result.setConfiguration(configuration);

		return result;
	}

	private Trigger reverseTransformTrigger(SharedTrigger trigger, OpenHabCommandReverseTransformer commandTransformer) {
		Trigger result = new Trigger();
		result.setDescription(trigger.getDescription());
		result.setLabel(trigger.getLabel());
		result.setType(getReverseTriggerType(trigger.getTriggerTypeContainer().getTriggerType()));

		// configuration
		triggerReverseTransformer.convertTriggerTypeContainer(trigger.getTriggerTypeContainer(), commandTransformer);
		final Configuration configuration = new Configuration();
		trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().forEach((k, v) -> {
			configuration.setAdditionalProperty(k.getKeyString(), v);
		});
		result.setConfiguration(configuration);

		return result;
	}

	private Action reverseTransformAction(SharedAction sa, OpenHabCommandReverseTransformer commandTransformer) {
		final Action action = new Action();
		action.setDescription(sa.getDescription());
		action.setLabel(sa.getLabel());
		action.setType(getReverseActionType(sa.getActionTypeContainer().getActionType()));

		// configuration
		actionReverseTransformer.convertActionTypeContainer(sa.getActionTypeContainer(), commandTransformer);
		final Configuration configuration = new Configuration();
		sa.getActionTypeContainer().getActionTypeSpecificValues().forEach((k, v) -> {
			configuration.setAdditionalProperty(k.getKeyString(), v);
		});
		action.setConfiguration(configuration);
		return action;
	}

	private String getReverseConditionType(ConditionType type) {
		switch (type) {
		case DayOfWeek:
			return "timer.DayOfWeekCondition";
		case ScriptEvaluatesTrue:
			return "script.ScriptCondition";
		case ItemState:
			return "core.ItemStateCondition";
		case TimeOfDay:
			return "core.TimeOfDayCondition";
		default:
			LOG.warn("Could not reverse transform condition type: " + type.name());
			return null;
		}
	}

	private String getReverseTriggerType(TriggerType triggerType) {
		switch (triggerType) {
		case ItemStateChanged:
			return "core.ItemStateChangeTrigger";
		case CommandReceived:
			return "core.ItemCommandTrigger";
		case ItemStateUpdated:
			return "core.ItemStateUpdateTrigger";
		case Timed:
			return "timer.TimeOfDayTrigger";
		case TriggerChannelFired:
			return "core.ChannelEventTrigger";
		default:
			LOG.warn("Could not reverse transform trigger type: " + triggerType);
			return "";
		}
	}

	private String getReverseActionType(ActionType actionType) {
		switch (actionType) {
		case EnableDisableRule:
			return "core.RuleEnablementAction";
		case ExecuteScript:
			return "script.ScriptAction";
		case PlaySound:
			return "media.PlayAction";
		case RunRules:
			return "core.RunRuleAction";
		case SaySomething:
			return "media.SayAction";
		case ItemCommand:
			return "core.ItemCommandAction";
		default:
			LOG.warn("Could not reverse transform trigger type: " + actionType);
			return "";
		}
	}

	private Status getStatus(SharedRule rule) {
		if (rule.getStatus() == null || rule.getStatus().isEmpty() || "-".equals(rule.getStatus())) {
			return null;
		}
		Status result = new Status();
		result.setStatus(rule.getStatus());
		return result;
	}
}
