package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.UiEntrypointController;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;

/** @author Steffen Jacobs */
public class UiEntrypoint {

	private final static Logger LOG = LoggerFactory.getLogger(UiEntrypoint.class);

	private final UiFactory uiFactory;
	private final UiEntrypointController entrypointController;
	private final JTable rulesTable;
	private final JTable itemsTable;
	final RuleDetailsPanel ruleDetailsPanel;
	private final CodeEditor codeText;

	public UiEntrypoint() {
		final SettingService settingService = new SettingService("./settings.config");
		codeText = new CodeEditor(settingService);
		entrypointController = new UiEntrypointController(settingService, codeText);
		uiFactory = new UiFactory(settingService);

		rulesTable = uiFactory.createRulesTable();
		itemsTable = uiFactory.createItemsTable();
		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
	}

	private void setupDockingEnvironment(JFrame frame) {
		CControl control = new CControl(frame);
		frame.add(control.getContentArea());

		// create items table window
		SingleCDockable itemsTableWindow = createDockable("ItemTable-Window", "Items", itemsTable);
		control.addDockable(itemsTableWindow);

		// create rule table window
		SingleCDockable ruleTableWindow = createDockable("RuleTable-Window", "Rules", rulesTable);
		control.addDockable(ruleTableWindow);

		// create pseudo code window
		SingleCDockable pseudocodeWindow = createDockable("Pseudocode-Window", "Generated Pseudocode", codeText);
		control.addDockable(pseudocodeWindow);

		// create rule details window
		SingleCDockable ruleDetailsWindow = createDockable("RuleDetails-Window", "Rule Details", ruleDetailsPanel);
		control.addDockable(ruleDetailsWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, 1, 1, pseudocodeWindow);
		grid.add(0, 1, .5, 1, ruleTableWindow);
		grid.add(.5, 1, .5, 1, itemsTableWindow);
		grid.add(1, 0, 1, 2, ruleDetailsWindow);

		control.getContentArea().deploy(grid);
	}

	private SingleCDockable createDockable(String id, String title, JComponent component) {
		DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
		dockable.setTitleText(title);
		dockable.setCloseable(false);
		dockable.add(new JScrollPane(component));
		return dockable;
	}

	private void createAndShowGUI() {
		entrypointController.setUi(this);
		// Creating the Frame
		JFrame frame = setupFrame();

		// Creating the MenuBar and adding components
		frame.setJMenuBar(setupMenu(frame));

		// Rule Details Panel
		createRuleDetailsPanel();

		// setup docking environment
		setupDockingEnvironment(frame);

		frame.setVisible(true);
	}

	private JFrame setupFrame() {
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
		return frame;
	}

	private void createRuleDetailsPanel() {
		rulesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesTable.getSelectionModel().addListSelectionListener(e -> {
			SharedRule rule = entrypointController.getRuleByIndex(rulesTable.getSelectedRow());
			ruleDetailsPanel.setDisplayedRule(rule);
			entrypointController.renderPseudocode(rule);
		});
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
				entrypointController.loadOpenHABItems();
				entrypointController.loadOpenHABRules();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(frame, String.format("Error while trying to connect to '%s' (%s).\nYou can change the URL and the port under File -> Settings.",
						entrypointController.getUrlWithPort(), e2.getMessage()), "Could not connect to openHAB server.", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JMenuItem mImportFromHomeAssistant = new JMenuItem("HomeAssistant");
		mConnect.add(mImportFromHomeAssistant);
		mImportFromHomeAssistant.addActionListener(e -> {
			try {
				entrypointController.loadHomeAssistantData();
//				entrypointController.loadOpenHABRules();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(frame, String.format("Error while trying to connect to '%s' (%s).\nYou can change the URL and the port under File -> Settings.",
						entrypointController.getUrlWithPort(), e2.getMessage()), "Could not connect to HomeAssistant server.", JOptionPane.ERROR_MESSAGE);
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

	public void refreshItems(Iterable<SharedItem> loadedItems) {
		itemsTable.getSelectionModel().clearSelection();
		uiFactory.updateItemsTable(itemsTable, loadedItems);
	}

	public void refreshRulesTable(Iterable<SharedRule> loadedRules) {
		rulesTable.getSelectionModel().clearSelection();
		ruleDetailsPanel.setDisplayedRule(null);
		uiFactory.updateRuleTable(rulesTable, loadedRules);
	}
}
