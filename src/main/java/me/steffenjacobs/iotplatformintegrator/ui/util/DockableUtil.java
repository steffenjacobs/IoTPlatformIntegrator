package me.steffenjacobs.iotplatformintegrator.ui.util;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;

/** @author Steffen Jacobs */
public class DockableUtil {

	public static SingleCDockable createDockable(String id, String title, JComponent component) {
		DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
		dockable.setTitleText(title);
		dockable.setCloseable(false);
		dockable.add(new JScrollPane(component));
		return dockable;
	}
}
