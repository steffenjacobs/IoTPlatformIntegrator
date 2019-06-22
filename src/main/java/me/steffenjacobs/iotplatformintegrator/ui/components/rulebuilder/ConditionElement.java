package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionTypeContainer;

/** @author Steffen Jacobs */
public class ConditionElement extends DynamicElement {
	private static final long serialVersionUID = 76080274137145616L;

	public ConditionElement(RuleBuilder rb) {
		super();
		super.elementType.setText(ElementType.Condition.getDisplayString());
		super.addButton.addActionListener(e -> rb.appendDynamicElement(new ConditionElement(rb)));

		super.setColors(RuleColors.CONDITION_STRATEGY_PANEL_COLOR, RuleColors.CONDITION_HEADER_COLOR, RuleColors.CONDITION_COLOR, RuleColors.CONDITION_BORDER_COLOR);
	}

	public void setConditionTypeContainer(ConditionTypeContainer conditionTypeContainer) {
		super.subType.setText(conditionTypeContainer.getConditionType().name());
	}

}
