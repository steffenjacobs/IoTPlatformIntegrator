package me.steffenjacobs.iotplatformintegrator.ui.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** @author Steffen Jacobs */
public abstract class DocumentAdapter implements DocumentListener {

	@Override
	public final void insertUpdate(DocumentEvent e) {
		this.changedUpdate(e);
	}

	@Override
	public final void removeUpdate(DocumentEvent e) {
		this.changedUpdate(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

}
