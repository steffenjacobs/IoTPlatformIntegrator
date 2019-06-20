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
	private final JPanel ruleTableSourcePanel = new JPanel();
	private final JPanel ruleTableTargetPanel = new JPanel();

	@Override
	public void onAppear() {
		ConnectionExplorer connectionExplorer = GlobalComponentHolder.getInstance().getConnectionExplorer();
		connectionExplorerPanel.add(connectionExplorer, BorderLayout.CENTER);

		JTable ruleTableSource = GlobalComponentHolder.getInstance().getRuleTableSource();
		ruleTableSourcePanel.removeAll();
		ruleTableSourcePanel.add(new JScrollPane(ruleTableSource), BorderLayout.CENTER);

		JTable ruleTableTarget = GlobalComponentHolder.getInstance().getRuleTableTarget();
		ruleTableTargetPanel.removeAll();
		ruleTableTargetPanel.add(new JScrollPane(ruleTableTarget), BorderLayout.CENTER);
	}

	public AdoptionPerspective() {
		connectionExplorerPanel.setLayout(new BorderLayout());
		ruleTableSourcePanel.setLayout(new BorderLayout());
		ruleTableTargetPanel.setLayout(new BorderLayout());
		setupDockingEnvironment();
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorerPanel);
		control.addDockable(connectionExplorerWindow);

		// create source rule table window
		SingleCDockable ruleTableSourceWindow = DockableUtil.createDockable("RuleTableSource-Window", "Rules from Source", ruleTableSourcePanel);
		control.addDockable(ruleTableSourceWindow);

		// create target rule table window
		SingleCDockable ruleTableTargetWindow = DockableUtil.createDockable("RuleTableTarget-Window", "Rules from Target", ruleTableTargetPanel);
		control.addDockable(ruleTableTargetWindow);

		// create instance chooser window
		SingleCDockable instanceChooserWindow = DockableUtil.createDockable("InstanceChooser-Window", "Instance Chooser", GlobalComponentHolder.getInstance().getInstanceChooser());
		control.addDockable(instanceChooserWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, .4, .8, connectionExplorerWindow);
		grid.add(0, .8, .4, .2, instanceChooserWindow);
		grid.add(0, 1, .5, 1, ruleTableSourceWindow);
		grid.add(.5, 1, 1, 1, ruleTableTargetWindow);
		control.getContentArea().deploy(grid);
	}
}
