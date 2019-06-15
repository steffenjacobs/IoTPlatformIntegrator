package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.service.openhab.RuleStringifyService;
import me.steffenjacobs.iotplatformintegrator.ui.UiFactory;

/** @author Steffen Jacobs */
public class RuleDetailsPanel extends JPanel {
	private static final long serialVersionUID = -3955999116242608088L;

	private final JTextField txtRuleName;
	private final JTextField txtUUID;
	private final JTextField txtDescription;
	private final JTextField txtStatus;

	private final JPanel noRuleSelected, ruleSelected;
	private static final RuleStringifyService ruleStringifyService = new RuleStringifyService();

	public RuleDetailsPanel(UiFactory uiFactory) {
		super();

		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Name: ", form);
		txtRuleName = new JTextField();
		formUtility.addLastField(txtRuleName, form);

		formUtility.addLabel("UUID: ", form);
		txtUUID = new JTextField();
		formUtility.addLastField(txtUUID, form);

		formUtility.addLabel("Description: ", form);
		txtDescription = new JTextField();
		formUtility.addLastField(txtDescription, form);

		formUtility.addLabel("Status: ", form);
		txtStatus = new JTextField();
		formUtility.addLastField(txtStatus, form);

		form.setBorder(new EmptyBorder(2, 2, 2, 2));

		// Add form panel to rule panel
		ruleSelected = new JPanel();
		ruleSelected.add(form);
		ruleSelected.setBorder(BorderFactory.createTitledBorder("Rule Details"));

		noRuleSelected = new JPanel();
		noRuleSelected.add(new JLabel("Please select a rule to display the details.                                                                          "));

		super.add(noRuleSelected, BorderLayout.NORTH);
	}

	public void setDisplayedRule(ExperimentalRule rule) {
		if (rule == null) {
			super.removeAll();
			super.add(noRuleSelected, BorderLayout.NORTH);
		} else {
			txtRuleName.setText(rule.getName());
			txtUUID.setText(rule.getUid());
			txtDescription.setText(rule.getDescription());
			txtStatus.setText(ruleStringifyService.getReadeableStatus(rule));

			for (Condition c : rule.getConditions()) {
				System.out.println(c);
			}
			super.removeAll();
			super.add(ruleSelected, BorderLayout.NORTH);
		}
		this.repaint();
		this.revalidate();
	}

}
