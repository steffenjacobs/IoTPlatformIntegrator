package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class AdoptionPerspective extends Perspective {

	private final JPanel connectionExplorerPanel = new JPanel();

	@Override
	public void onAppear() {
		ConnectionExplorer connectionExplorer = GlobalComponentHolder.getInstance().getConnectionExplorer();
		connectionExplorerPanel.add(connectionExplorer, BorderLayout.CENTER);
	}

	public AdoptionPerspective() {
		connectionExplorerPanel.setLayout(new BorderLayout());
		setupDockingEnvironment();
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorerPanel);
		control.addDockable(connectionExplorerWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, .4, 1, connectionExplorerWindow);
		control.getContentArea().deploy(grid);
	}
}
