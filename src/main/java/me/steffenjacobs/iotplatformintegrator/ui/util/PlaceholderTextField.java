package me.steffenjacobs.iotplatformintegrator.ui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;

public class PlaceholderTextField extends JTextField {
	private static final long serialVersionUID = -6207362685187215534L;

	private final String placeholder;

	public PlaceholderTextField(final String placeholder) {
		this.placeholder = placeholder;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
			return;
		}

		final Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(Color.decode("#bababa"));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawString(placeholder, getInsets().left + 2, g.getFontMetrics().getMaxAscent() + getInsets().top);
	}

}
