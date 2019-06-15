package me.steffenjacobs.iotplatformintegrator.domain.shared;

import java.util.Set;

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
