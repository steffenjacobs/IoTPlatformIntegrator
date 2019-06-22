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
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
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

		// create source item table window
		SingleCDockable sourceItemWindow = DockableUtil.createDockable("SourceItemTable-Window", "Source Items", GlobalComponentHolder.getInstance().getItemSourceTable());
		control.addDockable(sourceItemWindow);

		// create target item table window
		SingleCDockable targetItemWindow = DockableUtil.createDockable("TargetItemTable-Window", "Target Items", GlobalComponentHolder.getInstance().getItemTargetTable());
		control.addDockable(targetItemWindow);

		// create rule builder window
		SingleCDockable ruleBuilderWindow = DockableUtil.createDockable("RuleBuilder-Window", "RuleBuilder", new RuleBuilder());
		control.addDockable(ruleBuilderWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, .15, .4, connectionExplorerWindow);
		grid.add(0, .4, .15, .3, instanceChooserWindow);
		grid.add(0, .7, .3, .3, ruleTableSourceWindow);
		grid.add(.3, .7, .3, .3, ruleTableTargetWindow);
		
		grid.add(.15, 0, .65, .7, ruleBuilderWindow);
		
		grid.add(.8, 0, .2, .5, sourceItemWindow);
		grid.add(.8, .5, .2, .5, targetItemWindow);
		
		control.getContentArea().deploy(grid);
	}
}
