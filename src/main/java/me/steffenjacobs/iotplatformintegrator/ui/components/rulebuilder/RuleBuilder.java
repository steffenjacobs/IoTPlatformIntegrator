package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleRenderController;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleRenderController.ConditionElement;

/** @author Steffen Jacobs */
public class RuleBuilder extends JPanel {
	private static final long serialVersionUID = 7338245976135038651L;

	private final JPanel rulePanel = new JPanel();

	public RuleBuilder() {

		this.add(rulePanel);
		new RuleRenderController(this);
	}

	public void appendConditionElement(ConditionElement conditionElement) {
		rulePanel.add(conditionElement);
	}

	public void clear() {
		rulePanel.removeAll();
	}

	public void setHeader(String ruleName, String ruleStatus, String ruleDescription) {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout());
		headerPanel.add(new JLabel("Name: " + ruleName));
		headerPanel.add(new JLabel("Status: " + ruleStatus));
		headerPanel.add(new JLabel("Description: " + ruleDescription));
		// TODO: beautify
		rulePanel.add(headerPanel);
	}
}
