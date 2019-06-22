package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionTypeContainer;

/** @author Steffen Jacobs */
public class ConditionElement extends DynamicElement {
	private static final long serialVersionUID = 76080274137145616L;

	public ConditionElement(UUID uuid) {
		super(ElementType.Condition, uuid);

		super.setColors(RuleColors.CONDITION_STRATEGY_PANEL_COLOR, RuleColors.CONDITION_HEADER_COLOR, RuleColors.CONDITION_COLOR, RuleColors.CONDITION_BORDER_COLOR);
	}

	public void setConditionTypeContainer(ConditionTypeContainer conditionTypeContainer) {
		super.subType.setText(conditionTypeContainer.getConditionType().name());
	}

}
