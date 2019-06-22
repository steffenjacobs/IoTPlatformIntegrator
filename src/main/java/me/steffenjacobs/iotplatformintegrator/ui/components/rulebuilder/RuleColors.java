
package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.Color;

/** @author Steffen Jacobs */
public interface RuleColors {

	static Color CONDITION_COLOR = ColorPalette.MAIN_COLOR;
	static Color CONDITION_BORDER_COLOR = brighten(ColorPalette.MAIN_COLOR, -.2);
	static Color CONDITION_HEADER_COLOR = brighten(ColorPalette.MAIN_COLOR, .1);
	static Color CONDITION_STRATEGY_PANEL_COLOR = brighten(ColorPalette.MAIN_COLOR, .1);

	static Color brighten(Color color, double f) {
		int red = (int) Math.round(Math.min(255, color.getRed() + 255 * f));
		int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * f));
		int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * f));

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);
	}

}
