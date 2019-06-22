package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionTypeContainer;

/** @author Steffen Jacobs */
public class ActionElement extends DynamicElement {
	private static final long serialVersionUID = 26080274137145616L;

	public ActionElement(RuleBuilder rb) {
		super(ElementType.Action);

		super.setColors(RuleColors.ACTION_STRATEGY_PANEL_COLOR, RuleColors.ACTION_HEADER_COLOR, RuleColors.ACTION_COLOR, RuleColors.ACTION_BORDER_COLOR);
	}

	public void setActionTypeContainer(ActionTypeContainer actionTypeContainer) {
		super.subType.setText(actionTypeContainer.getActionType().name());
	}

}
