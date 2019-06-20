package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import javax.swing.JFrame;

import bibliothek.gui.dock.common.CControl;

/** @author Steffen Jacobs */
public class Perspective {
	protected CControl control;

	public void addToFrame(JFrame frame) {
		frame.add(control.getContentArea());
	}

	public void removeFromFrame(JFrame frame) {
		frame.remove(control.getContentArea());
	}
}
