package me.steffenjacobs.iotplatformintegrator.service.manage.events;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class RuleChangeEvent extends WithSharedRuleEvent {

	public static enum ChangeOperation {
		ADD, REMOVE, UPDATE;
	}

	private final SharedRuleElement changedElement;
	private final ChangeOperation operation;
	private final SharedRuleElement oldElement;

	public RuleChangeEvent(SharedRule sharedRule, SharedRuleElement changedElement, ChangeOperation operation) {
		this(sharedRule, changedElement, changedElement, operation);
	}

	public RuleChangeEvent(SharedRule sharedRule, SharedRuleElement newElement, SharedRuleElement oldElement, ChangeOperation operation) {
		super(EventType.RuleChangeEvent, sharedRule);
		this.changedElement = newElement;
		this.oldElement = oldElement;
		this.operation = operation;
	}

	public SharedRuleElement getChangedElement() {
		return changedElement;
	}

	public SharedRuleElement getOldElement() {
		return oldElement;
	}

	public ChangeOperation getOperation() {
		return operation;
	}

}
