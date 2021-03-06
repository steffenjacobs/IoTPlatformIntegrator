package me.steffenjacobs.iotplatformintegrator.domain.shared.rule;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;

/** @author Steffen Jacobs */
public class SharedRule {

	private final String name;
	private final String id;
	private final String description;
	private final String visible;
	private final String status;

	private final Set<SharedTrigger> triggers;
	private final Set<SharedCondition> conditions;
	private final Set<SharedAction> actions;

	public SharedRule(String name, String id, String description, String visible, String status, Set<SharedTrigger> triggers, Set<SharedCondition> conditions,
			Set<SharedAction> actions) {
		super();
		this.name = name;
		this.id = id;
		this.description = description;
		this.visible = visible;
		this.status = status;
		this.triggers = triggers;
		this.conditions = conditions;
		this.actions = actions;
	}

	/** Copy constructor. */
	public SharedRule(SharedRule rule) {
		this(rule.getName(), rule.getId(), rule);
	}

	/**
	 * Copy constructor with a {@link String new rule name} that also generates a
	 * new ID and copies the changes from {@link SharedRule rule}.
	 */
	public SharedRule(String newRuleName, SharedRule rule) {
		this(newRuleName, UUID.randomUUID().toString(), rule);
	}

	private SharedRule(String newRuleName, String newRuleId, SharedRule rule) {
		this.name = newRuleName;
		this.id = newRuleId;
		this.description = rule.getDescription();
		this.visible = rule.getVisible();
		this.status = rule.getStatus();
		this.triggers = new HashSet<SharedTrigger>();
		this.conditions = new HashSet<SharedCondition>();
		this.actions = new HashSet<SharedAction>();

		rule.getActions().forEach(a -> this.actions.add(new SharedAction(a)));
		rule.getConditions().forEach(a -> this.conditions.add(new SharedCondition(a)));
		rule.getTriggers().forEach(a -> this.triggers.add(new SharedTrigger(a)));

	}

	/**
	 * Constructor to create a dummy role with an {@String error message} if parsing
	 * went wrong.
	 */
	public SharedRule(String errorMessage) {
		this(errorMessage, "", "", "", "", new HashSet<SharedTrigger>(), new HashSet<SharedCondition>(), new HashSet<SharedAction>());
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getVisible() {
		return visible;
	}

	public String getStatus() {
		return status;
	}

	public Set<SharedTrigger> getTriggers() {
		return triggers;
	}

	public Set<SharedCondition> getConditions() {
		return conditions;
	}

	public Set<SharedAction> getActions() {
		return actions;
	}

}
