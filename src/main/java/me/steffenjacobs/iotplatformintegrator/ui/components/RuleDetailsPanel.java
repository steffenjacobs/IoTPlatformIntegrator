package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

		ruleSelected = new JPanel();

		// setup grid layout
		ruleSelected.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;

		txtRuleName = new JTextField();
		ruleSelected.add(new GridPane("Name:", txtRuleName), gbc);
		gbc.gridy++;

		txtUUID = new JTextField();
		ruleSelected.add(new GridPane("UUID:", txtUUID), gbc);
		gbc.gridy++;

		txtDescription = new JTextField();
		ruleSelected.add(new GridPane("Description:", txtDescription));
		gbc.gridy++;

		txtStatus = new JTextField();
		ruleSelected.add(new GridPane("Status:", txtStatus));

		ruleSelected.setBorder(BorderFactory.createTitledBorder("Rule Details"));

		noRuleSelected = new JPanel();
		noRuleSelected.add(new JLabel("Please select a rule to display the details."));

		super.add(noRuleSelected);
	}

	private class GridPane extends JPanel {
		private static final long serialVersionUID = 4839797903102457482L;

		public GridPane(String label, JComponent comp) {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			add(new JLabel(label), gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.EAST;
			add(comp, gbc);
		}

	}

	public void setDisplayedRule(ExperimentalRule rule) {
		if (rule == null) {
			super.removeAll();
			super.add(noRuleSelected);
		} else {
			txtRuleName.setText(rule.getName());
			txtUUID.setText(rule.getUid());
			txtDescription.setText(rule.getDescription());
			txtStatus.setText(ruleStringifyService.getReadeableStatus(rule));

			for (Condition c : rule.getConditions()) {
				System.out.println(c);
			}
			super.removeAll();
			super.add(ruleSelected);
		}
		this.repaint();
		this.revalidate();
	}

}
