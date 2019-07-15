package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class ScoreboardPerspective extends Perspective {

	public ScoreboardPerspective() {
		setupDockingEnvironment();
	}

	@Override
	public void onAppear() {
	}

	private void setupDockingEnvironment() {
		control = new CControl();

		// create scoreboard window
		final SingleCDockable scoreboardWindow = DockableUtil.createDockable("Scoreboard-Window", "Scoreboard", GlobalComponentHolder.getInstance().getScoreboardPanel());
		control.addDockable(scoreboardWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		grid.add(0, 0, 1, 1, scoreboardWindow);

		control.getContentArea().deploy(grid);
	}
}
