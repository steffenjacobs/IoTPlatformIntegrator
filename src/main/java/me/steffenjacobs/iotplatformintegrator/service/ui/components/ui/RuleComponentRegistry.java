package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.manage.RuleRelatedAnnotation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

/** @author Steffen Jacobs */
public interface RuleComponentRegistry {

	void addAnnotatedComponent(Component component, SharedTypeSpecificKey ruleElementSpecificKey, int index);

	void clearComponents();

	RuleRelatedAnnotation getAnnotationFromComponent(Component comp);

	SharedRuleElement removeRuleElementById(UUID uuid);

	SharedRuleElement getRuleElementById(UUID uuid);

}
