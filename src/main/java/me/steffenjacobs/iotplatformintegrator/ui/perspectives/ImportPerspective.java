package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangedEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.ImportPerspectiveController;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.UiFactory;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class ImportPerspective extends Perspective {

	private final JTable rulesTable;
	private final JTable itemsTable;
	private final RuleDetailsPanel ruleDetailsPanel;
	private final CodeEditor codeText;
	private final ImportPerspectiveController perpsectiveController;
	private final UiFactory uiFactory;

	public ImportPerspective(SettingService settingService, UiFactory uiFactory) {
		this.uiFactory = uiFactory;
		this.perpsectiveController = new ImportPerspectiveController(settingService);

		codeText = new CodeEditor(settingService);
		CodeEditorController controller = new CodeEditorController(codeText, settingService);
		codeText.setController(controller);

		rulesTable = uiFactory.createRulesTable();
		itemsTable = uiFactory.createItemsTable();
		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
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
			if (perpsectiveController.getLastSelectedRule() != rule) {
				EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(rule));
			}
		});
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create items table window
		SingleCDockable itemsTableWindow = DockableUtil.createDockable("ItemTable-Window", "Items", itemsTable);
		control.addDockable(itemsTableWindow);

		// create rule table window
		SingleCDockable ruleTableWindow = DockableUtil.createDockable("RuleTable-Window", "Rules", rulesTable);
		control.addDockable(ruleTableWindow);

		// create pseudo code window
		SingleCDockable pseudocodeWindow = DockableUtil.createDockable("Pseudocode-Window", "Generated Pseudocode", codeText);
		control.addDockable(pseudocodeWindow);

		// create rule details window
		SingleCDockable ruleDetailsWindow = DockableUtil.createDockable("RuleDetails-Window", "Rule Details", ruleDetailsPanel);
		control.addDockable(ruleDetailsWindow);

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer",
				GlobalComponentHolder.getInstance().getConnectionExplorer());
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

	public void refreshItems(Iterable<SharedItem> loadedItems) {
		itemsTable.getSelectionModel().clearSelection();
		uiFactory.updateItemsTable(itemsTable, loadedItems);
	}

	public void refreshRulesTable(Iterable<SharedRule> loadedRules) {
		rulesTable.getSelectionModel().clearSelection();
		uiFactory.updateRuleTable(rulesTable, loadedRules);
		EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(null));
	}

	public void resetCodeEditor() {
		codeText.showHelpText();
	}

}
