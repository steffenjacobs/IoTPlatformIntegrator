package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ExportRuleToPlatformEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectTargetRuleEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleBuilderRenderController;

/** @author Steffen Jacobs */
public class RuleBuilder extends JPanel {
	private static final long serialVersionUID = 7338245976135038651L;

	private final JPanel rulePanel = new JPanel();
	private RuleBuilderRenderController controller;

	private final JPanel buttonBar;

	public RuleBuilder() {
		this.setLayout(new BorderLayout());
		rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.Y_AXIS));

		final JPanel rulePanelWrapper = new JPanel();

		rulePanelWrapper.add(rulePanel);
		this.add(rulePanelWrapper, BorderLayout.CENTER);

		buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

		final JButton exportToPlatformButton = new JButton("Export to Platform");
		exportToPlatformButton.addActionListener(e -> {
			Set<ServerConnection> connections = App.getServerConnectionCache().getConnections();
			final JComboBox<ServerConnection> jcb = new JComboBox<>(connections.toArray(new ServerConnection[connections.size()]));
			jcb.setEditable(true);
			JOptionPane.showMessageDialog(null, jcb, "select or type a value", JOptionPane.QUESTION_MESSAGE);

			boolean result = controller.validateForPlatformExport(((ServerConnection) jcb.getSelectedItem()).getItemDirectory());
			if (result) {
				String name = JOptionPane.showInputDialog(this, "Enter a name for the new rule:", controller.getDisplayedRule().get().getName());
				EventBus.getInstance().fireEvent(new ExportRuleToPlatformEvent((ServerConnection) jcb.getSelectedItem(), controller.getDisplayedRule().get(), name));
			}
		});
		buttonBar.add(exportToPlatformButton);

		final JButton exportToDatabaseButton = new JButton("Store rule to Database");
		exportToDatabaseButton.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(this, "Enter a name for the new rule:", controller.getDisplayedRule().get().getName());
			EventBus.getInstance().fireEvent(new StoreRuleToDatabaseEvent(controller.getDisplayedRule().get(), name, true));
		});
		buttonBar.add(exportToDatabaseButton);

		final JButton selectTargetRuleButton = new JButton("Select Target Rule");
		selectTargetRuleButton.addActionListener(e -> {
			EventBus.getInstance().fireEvent(new SelectTargetRuleEvent());
		});

		buttonBar.add(selectTargetRuleButton);
		buttonBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		this.add(buttonBar, BorderLayout.SOUTH);
		buttonBar.setEnabled(false);

		new RuleBuilderRenderController(this);
	}

	public void onSelectedRuleChanged(SharedRule rule) {
		buttonBar.setEnabled(rule != null);
	}

	public void appendDynamicElement(DynamicElement dynamicElement) {
		rulePanel.add(dynamicElement);
		super.revalidate();
		super.repaint();
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

	public void setRenderController(RuleBuilderRenderController ruleBuilderRenderController) {
		this.controller = ruleBuilderRenderController;

	}

}
