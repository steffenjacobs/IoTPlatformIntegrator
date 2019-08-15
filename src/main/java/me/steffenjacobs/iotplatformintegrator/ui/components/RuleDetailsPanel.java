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

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;

/** @author Steffen Jacobs */
public class RuleDetailsPanel extends JPanel {
	private static final long serialVersionUID = -3955999116242608088L;

	private final JTextField txtRuleName;
	private final JTextField txtUUID;
	private final JTextField txtDescription;
	private final JTextField txtStatus;

	private final JPanel conditionsPanel;
	private final JPanel triggersPanel;
	private final JPanel actionsPanel;

	private final JPanel noRuleSelected, ruleSelected;

	private SharedRule rule = null;

	public RuleDetailsPanel() {
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
		conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
		triggersPanel = new JPanel();
		ruleSelected.add(triggersPanel);
		triggersPanel.setLayout(new BoxLayout(triggersPanel, BoxLayout.Y_AXIS));
		actionsPanel = new JPanel();
		ruleSelected.add(actionsPanel);
		actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

		ruleSelected.setBorder(BorderFactory.createTitledBorder("Rule Details"));
		ruleSelected.setLayout(new BoxLayout(ruleSelected, BoxLayout.Y_AXIS));

		noRuleSelected = new JPanel();
		noRuleSelected.add(new JLabel("Please select a rule to display the details.                                                                          "));

		super.add(noRuleSelected, BorderLayout.NORTH);

		EventBus.getInstance().addEventHandler(EventType.SELECTED_RULE_CHANGE, e -> setDisplayedRule(((SelectedRuleChangeEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RULE_CHANGE, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (rule == event.getSelectedRule()) {
				setDisplayedRule(event.getSelectedRule());
			}
		});
	}

	private void setDisplayedRule(SharedRule rule) {
		this.rule = rule;
		if (rule == null) {
			super.removeAll();
			super.add(noRuleSelected, BorderLayout.NORTH);
		} else {
			txtRuleName.setText(rule.getName());
			txtUUID.setText(rule.getId());
			txtDescription.setText(rule.getDescription());
			txtStatus.setText(rule.getStatus());

			triggersPanel.removeAll();
			conditionsPanel.removeAll();
			actionsPanel.removeAll();
			rule.getTriggers().forEach(t -> triggersPanel.add(createTriggerPanel(t)));
			rule.getConditions().forEach(c -> conditionsPanel.add(createConditionPanel(c)));
			rule.getActions().forEach(a -> actionsPanel.add(createActionPanel(a)));

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
		formUtility.addLastField(new JTextField("" + sc.getConditionTypeContainer().getConditionType()), form);

		formUtility.addLabel("Label: ", form);
		formUtility.addLastField(new JTextField(sc.getLabel()), form);

		for (ConditionTypeSpecificKey key : sc.getConditionTypeContainer().getConditionType().getTypeSpecificKeys()) {
			formUtility.addLabel(key.getDisplayString(), form);
			formUtility.addLastField(new JTextField("" + sc.getConditionTypeContainer().getConditionTypeSpecificValues().get(key)), form);
		}

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("CONDITION"));
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
		formUtility.addLastField(new JTextField("" + st.getTriggerTypeContainer().getTriggerType()), form);

		formUtility.addLabel("Label: ", form);
		formUtility.addLastField(new JTextField(st.getLabel()), form);

		for (TriggerTypeSpecificKey key : st.getTriggerTypeContainer().getTriggerType().getTypeSpecificKeys()) {
			formUtility.addLabel(key.getDisplayString(), form);
			formUtility.addLastField(new JTextField("" + st.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(key)), form);
		}

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("TRIGGER"));
		panel.add(new JLabel(st.getDescription()));
		panel.add(Box.createVerticalStrut(10));
		panel.add(form);

		return panel;
	}

	private JPanel createActionPanel(SharedAction sa) {
		JPanel panel = new JPanel();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Type: ", form);
		formUtility.addLastField(new JTextField("" + sa.getActionTypeContainer().getActionType()), form);

		formUtility.addLabel("Label: ", form);
		formUtility.addLastField(new JTextField(sa.getLabel()), form);

		for (ActionTypeSpecificKey key : sa.getActionTypeContainer().getActionType().getTypeSpecificKeys()) {
			formUtility.addLabel(key.getDisplayString(), form);
			formUtility.addLastField(new JTextField("" + sa.getActionTypeContainer().getActionTypeSpecificValues().get(key)), form);
		}

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("ACTION"));
		panel.add(new JLabel(sa.getDescription()));
		panel.add(Box.createVerticalStrut(10));
		panel.add(form);

		return panel;
	}

}
