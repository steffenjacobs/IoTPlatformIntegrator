package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class AdoptionPerspective extends Perspective {

	private final JPanel connectionExplorerPanel = new JPanel();
	private final JPanel ruleTablePanel = new JPanel();

	@Override
	public void onAppear() {
		ConnectionExplorer connectionExplorer = GlobalComponentHolder.getInstance().getConnectionExplorer();
		connectionExplorerPanel.add(connectionExplorer, BorderLayout.CENTER);
		JTable ruleTable = GlobalComponentHolder.getInstance().getRuleTable();
		ruleTablePanel.removeAll();
		ruleTablePanel.add(new JScrollPane(ruleTable), BorderLayout.CENTER);
	}

	public AdoptionPerspective() {
		connectionExplorerPanel.setLayout(new BorderLayout());
		ruleTablePanel.setLayout(new BorderLayout());
		setupDockingEnvironment();
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorerPanel);
		control.addDockable(connectionExplorerWindow);

		// create rule table window
		SingleCDockable ruleTableWindow = DockableUtil.createDockable("RuleTable-Window", "Rules", ruleTablePanel);
		control.addDockable(ruleTableWindow);

		// create instance chooser window
		SingleCDockable instanceChooserWindow = DockableUtil.createDockable("InstanceChooser-Window", "Instance Chooser", GlobalComponentHolder.getInstance().getInstanceChooser());
		control.addDockable(instanceChooserWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, .4, .8, connectionExplorerWindow);
		grid.add(0, .8, .4, .2, instanceChooserWindow);
		grid.add(0, 1, .5, 1, ruleTableWindow);
		control.getContentArea().deploy(grid);
	}
}
