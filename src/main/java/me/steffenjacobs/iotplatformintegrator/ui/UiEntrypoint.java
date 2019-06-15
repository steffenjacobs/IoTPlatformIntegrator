package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.UiEntrypointController;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;

/** @author Steffen Jacobs */
public class UiEntrypoint {

	private final static Logger LOG = LoggerFactory.getLogger(UiEntrypoint.class);

	private final UiFactory uiFactory;
	private final UiEntrypointController entrypointController;
	private final JTable rulesTable;
	final RuleDetailsPanel ruleDetailsPanel;

	public UiEntrypoint() {
		final SettingService settingService = new SettingService("./settings.config");
		entrypointController = new UiEntrypointController(settingService);
		uiFactory = new UiFactory(settingService);

		rulesTable = uiFactory.createRulesTable();
		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
	}

	private void createAndShowGUI() {
		entrypointController.setUi(this);
		// Creating the Frame
		JFrame frame = new JFrame("IoT Platform Integrator");

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			LOG.error("Error while setting look and feel: " + e.getMessage(), e);
		}
		SwingUtilities.updateComponentTreeUI(frame);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 900);

		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("FILE");
		JMenu m2 = new JMenu("Help");
		mb.add(m1);
		mb.add(m2);
		JMenu mImportRules = new JMenu("Import Rules");

		JMenuItem mImportRulesFromOpenhab = new JMenuItem("OpenHAB");
		mImportRules.add(mImportRulesFromOpenhab);
		mImportRulesFromOpenhab.addActionListener(e -> entrypointController.loadOpenHABRules());

		JMenuItem mSettings = new JMenuItem("Settings");
		mSettings.addActionListener(e -> uiFactory.createSettingsFrame().setVisible(true));

		m1.add(mImportRules);
		m1.add(mSettings);

		// Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("Enter Text");
		JTextField tf = new JTextField(10); // accepts upto 10 characters
		JButton send = new JButton("Send");
		JButton reset = new JButton("Reset");
		panel.add(label); // Components Added using Flow Layout
		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);
		panel.add(reset);

		// Rule Details Panel
		rulesTable.getSelectionModel().addListSelectionListener(e -> ruleDetailsPanel.setDisplayedRule(entrypointController.getRuleByIndex(e.getFirstIndex())));

		// Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.WEST, new JScrollPane(rulesTable));
		frame.getContentPane().add(BorderLayout.EAST, new JScrollPane(ruleDetailsPanel));
		frame.setVisible(true);
	}

	public void createAndShowGUIAsync() {
		javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	public void refreshTable(List<ExperimentalRule> rules) {
		ruleDetailsPanel.setDisplayedRule(null);
		uiFactory.updateRuleTable(rulesTable, rules);
	}
}
