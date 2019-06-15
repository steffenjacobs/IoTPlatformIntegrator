package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Condition;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.Trigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.openhab.OpenHabRuleTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.openhab.RuleStringifyService;
import me.steffenjacobs.iotplatformintegrator.ui.UiFactory;

/** @author Steffen Jacobs */
public class RuleDetailsPanel extends JPanel {
	private static final long serialVersionUID = -3955999116242608088L;

	private final JTextField txtRuleName;
	private final JTextField txtUUID;
	private final JTextField txtDescription;
	private final JTextField txtStatus;

	private final JPanel conditionsPanel;
	private final JPanel triggersPanel;

	private final JPanel noRuleSelected, ruleSelected;
	private static final RuleStringifyService ruleStringifyService = new RuleStringifyService();
	private static final OpenHabRuleTransformationAdapter transformer = new OpenHabRuleTransformationAdapter();

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

		conditionsPanel = new JPanel();
		ruleSelected.add(conditionsPanel);
		triggersPanel = new JPanel();
		ruleSelected.add(triggersPanel);

		ruleSelected.setBorder(BorderFactory.createTitledBorder("Rule Details"));
		ruleSelected.setLayout(new BoxLayout(ruleSelected, BoxLayout.Y_AXIS));

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

			conditionsPanel.removeAll();
			for (Condition c : rule.getConditions()) {
				conditionsPanel.add(createConditionPanel(transformer.transformCondition(c)));
			}

			triggersPanel.removeAll();
			for (Trigger t : rule.getTriggers()) {
				triggersPanel.add(createTriggerPanel(transformer.transformTrigger(t)));
			}
			super.removeAll();
			super.add(ruleSelected, BorderLayout.NORTH);
		}
		this.repaint();
		this.revalidate();
	}

	private JPanel createConditionPanel(SharedCondition sc) {
		JPanel panel = new JPanel();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Type: ", form);
		formUtility.addLastField(new JTextField(sc.getType()), form);

		formUtility.addLabel("Label: ", form);
		formUtility.addLastField(new JTextField(sc.getLabel()), form);

		formUtility.addLabel("ItemName: ", form);
		formUtility.addLastField(new JTextField(sc.getItemName()), form);

		formUtility.addLabel("Operator: ", form);
		formUtility.addLastField(new JTextField(sc.getOperator()), form);

		formUtility.addLabel("State: ", form);
		formUtility.addLastField(new JTextField(sc.getState()), form);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Condition"));
		panel.add(new JLabel(sc.getDescription()));
		panel.add(Box.createVerticalStrut(10));
		panel.add(form);

		return panel;
	}

	private JPanel createTriggerPanel(SharedTrigger st) {
		JPanel panel = new JPanel();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Type: ", form);
		formUtility.addLastField(new JTextField(st.getType()), form);

		formUtility.addLabel("Label: ", form);
		formUtility.addLastField(new JTextField(st.getLabel()), form);

		formUtility.addLabel("ItemName: ", form);
		formUtility.addLastField(new JTextField(st.getItemName()), form);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Trigger"));
		panel.add(new JLabel(st.getDescription()));
		panel.add(Box.createVerticalStrut(10));
		panel.add(form);

		return panel;
	}

}
