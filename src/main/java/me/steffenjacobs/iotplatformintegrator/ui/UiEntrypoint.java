package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
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
	private final JTextArea codeText;

	public UiEntrypoint() {
		final SettingService settingService = new SettingService("./settings.config");
		entrypointController = new UiEntrypointController(settingService);
		uiFactory = new UiFactory(settingService);

		rulesTable = uiFactory.createRulesTable();
		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
		codeText = new JTextArea("Select a rule to see the generated pseudocode.");
	}

	private void createAndShowGUI() {
		entrypointController.setUi(this);
		// Creating the Frame
		JFrame frame = new JFrame("IoT Platform Integrator");
		try {
			BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/icon.png"));
			Graphics2D g = bufferedImage.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			frame.setIconImage(bufferedImage);
		} catch (IOException e1) {
			LOG.error("Could not load icon: " + e1.getMessage());
		}

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			LOG.error("Error while setting look and feel: " + e.getMessage(), e);
		}
		SwingUtilities.updateComponentTreeUI(frame);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 900);

		// Creating the MenuBar and adding components
		JMenuBar mb = setupMenu(frame);

		// Creating the panel at bottom and adding components
		JPanel panel = createBottomPanel();

		// Rule Details Panel
		createRuleDetailsPanel();

		// Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.WEST, new JScrollPane(rulesTable));
		frame.getContentPane().add(BorderLayout.EAST, new JScrollPane(ruleDetailsPanel));
		frame.getContentPane().add(BorderLayout.CENTER, new JScrollPane(codeText));
		frame.setVisible(true);
	}

	private void createRuleDetailsPanel() {
		rulesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesTable.getSelectionModel().addListSelectionListener(e -> {
			SharedRule rule = entrypointController.getRuleByIndex(rulesTable.getSelectedRow());
			ruleDetailsPanel.setDisplayedRule(rule);
			codeText.setText(entrypointController.getPseudocode(rule));
		});
	}

	private JPanel createBottomPanel() {
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
		return panel;
	}

	private JMenuBar setupMenu(JFrame frame) {
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("File");
		JMenu m2 = new JMenu("Help");
		mb.add(m1);
		mb.add(m2);
		JMenu mConnect = new JMenu("Connect");

		JMenuItem mImportFromOpenhab = new JMenuItem("OpenHAB");
		mConnect.add(mImportFromOpenhab);
		mImportFromOpenhab.addActionListener(e -> {
			try {
				entrypointController.loadOpenHABRules();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(frame, String.format("Error while trying to connect to '%s' (%s).\nYou can change the URL and the port under File -> Settings.",
						entrypointController.getUrlWithPort(), e2.getMessage()), "Could not connect to openHAB server.", JOptionPane.ERROR_MESSAGE);
			}
		});

		JMenuItem mSettings = new JMenuItem("Settings");
		mSettings.addActionListener(e -> uiFactory.createSettingsFrame().setVisible(true));

		m1.add(mConnect);
		m1.add(mSettings);
		return mb;
	}

	public void createAndShowGUIAsync() {
		javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
	}

	public void refreshTable(List<SharedRule> loadedRules) {
		rulesTable.getSelectionModel().clearSelection();
		ruleDetailsPanel.setDisplayedRule(null);
		uiFactory.updateRuleTable(rulesTable, loadedRules);
	}
}
