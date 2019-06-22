package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerTypeContainer;

/** @author Steffen Jacobs */
public class TriggerElement extends DynamicElement {
	private static final long serialVersionUID = 16080274137145616L;

	public TriggerElement(UUID uuid) {
		super(ElementType.Trigger, uuid);

		super.setColors(RuleColors.TRIGGER_STRATEGY_PANEL_COLOR, RuleColors.TRIGGER_HEADER_COLOR, RuleColors.TRIGGER_COLOR, RuleColors.TRIGGER_BORDER_COLOR);
	}

	public void setTriggerTypeContainer(TriggerTypeContainer triggerTypeContainer) {
		super.subType.setText(triggerTypeContainer.getTriggerType().name());
	}
}
