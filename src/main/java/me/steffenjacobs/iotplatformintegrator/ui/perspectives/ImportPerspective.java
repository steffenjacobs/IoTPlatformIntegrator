package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.ImportPerspectiveController;
import me.steffenjacobs.iotplatformintegrator.ui.UiFactory;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;
import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;

/** @author Steffen Jacobs */
public class ImportPerspective {

	private final JTable rulesTable;
	private final JTable itemsTable;
	private final RuleDetailsPanel ruleDetailsPanel;
	private final ConnectionExplorer connectionExplorer;
	private final CodeEditor codeText;
	private final ImportPerspectiveController perpsectiveController;
	private final UiFactory uiFactory;

	private CControl control;

	public ImportPerspective(SettingService settingService, UiFactory uiFactory) {
		this.uiFactory = uiFactory;
		this.perpsectiveController = new ImportPerspectiveController(settingService);
		codeText = new CodeEditor(settingService);
		rulesTable = uiFactory.createRulesTable();
		itemsTable = uiFactory.createItemsTable();
		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
		connectionExplorer = new ConnectionExplorer(perpsectiveController);
		perpsectiveController.setCodeText(codeText);
		perpsectiveController.setImportPerspective(this);

		setup();
	}

	public ImportPerspectiveController getPerpsectiveController() {
		return perpsectiveController;
	}

	private void setup() {
		// Rule Details Panel
		createRuleDetailsPanel();

		// setup layout
		setupDockingEnvironment();
	}

	private void createRuleDetailsPanel() {
		rulesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesTable.getSelectionModel().addListSelectionListener(e -> {
			SharedRule rule = perpsectiveController.getRuleByIndex(rulesTable.getSelectedRow());
			ruleDetailsPanel.setDisplayedRule(rule);
			perpsectiveController.renderPseudocode(rule);
		});
	}

	public void addToFrame(JFrame frame) {
		frame.add(control.getContentArea());
	}

	private void setupDockingEnvironment() {
		control = new CControl();

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

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorer);
		control.addDockable(connectionExplorerWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, .4, 1, connectionExplorerWindow);
		grid.add(.4, 0, 1, 1, pseudocodeWindow);
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

	public void refreshItems(Iterable<SharedItem> loadedItems) {
		itemsTable.getSelectionModel().clearSelection();
		uiFactory.updateItemsTable(itemsTable, loadedItems);
	}

	public void refreshRulesTable(Iterable<SharedRule> loadedRules) {
		rulesTable.getSelectionModel().clearSelection();
		ruleDetailsPanel.setDisplayedRule(null);
		uiFactory.updateRuleTable(rulesTable, loadedRules);
	}

	public void onConnectionEstablished(ServerConnection connection) {
		connectionExplorer.addConnection(connection);
	}

	public void resetCodeEditor() {
		codeText.showHelpText();
	}

	public void propagateRemovalOfServerConnection(ServerConnection serverConnection) {
		connectionExplorer.removeServerConnection(serverConnection);
	}
}
