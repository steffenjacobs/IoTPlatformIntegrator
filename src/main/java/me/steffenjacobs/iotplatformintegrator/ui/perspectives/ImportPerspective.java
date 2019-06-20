package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController;
import me.steffenjacobs.iotplatformintegrator.service.ui.ImportPerspectiveController;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.UiFactory;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;
import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class ImportPerspective extends Perspective {

	private final RuleDetailsPanel ruleDetailsPanel;
	private final CodeEditor codeText;
	private final ImportPerspectiveController perpsectiveController;
	private final JPanel connectionExplorerPanel = new JPanel();

	public ImportPerspective(SettingService settingService, UiFactory uiFactory) {
		this.perpsectiveController = new ImportPerspectiveController(settingService);

		codeText = new CodeEditor(settingService);
		CodeEditorController controller = new CodeEditorController(codeText, settingService);
		codeText.setController(controller);

		ruleDetailsPanel = new RuleDetailsPanel(uiFactory);
		perpsectiveController.setImportPerspective(this);

		connectionExplorerPanel.setLayout(new BorderLayout());
		setup();
	}

	@Override
	public void onAppear() {
		ConnectionExplorer connectionExplorer = GlobalComponentHolder.getInstance().getConnectionExplorer();
		connectionExplorerPanel.add(connectionExplorer, BorderLayout.CENTER);
	}

	public ImportPerspectiveController getPerpsectiveController() {
		return perpsectiveController;
	}

	private void setup() {
		// setup layout
		setupDockingEnvironment();
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create items table window
		SingleCDockable itemsTableWindow = DockableUtil.createDockable("ItemTable-Window", "Items", GlobalComponentHolder.getInstance().getItemTable());
		control.addDockable(itemsTableWindow);

		// create rule table window
		SingleCDockable ruleTableWindow = DockableUtil.createDockable("RuleTable-Window", "Rules", GlobalComponentHolder.getInstance().getRuleTable());
		control.addDockable(ruleTableWindow);

		// create pseudo code window
		SingleCDockable pseudocodeWindow = DockableUtil.createDockable("Pseudocode-Window", "Generated Pseudocode", codeText);
		control.addDockable(pseudocodeWindow);

		// create rule details window
		SingleCDockable ruleDetailsWindow = DockableUtil.createDockable("RuleDetails-Window", "Rule Details", ruleDetailsPanel);
		control.addDockable(ruleDetailsWindow);

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorerPanel);
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

	public void resetCodeEditor() {
		codeText.showHelpText();
	}

}
