package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** @author Steffen Jacobs */

public abstract class DynamicElement extends JPanel {
	private static final long serialVersionUID = 162937647608306926L;

	public enum ElementType {
		Trigger("Trigger"), Condition("Condition"), Action("Action");

		private final String displayString;

		private ElementType(String displayString) {
			this.displayString = displayString;

		}

		public String getDisplayString() {
			return displayString;
		}
	}

	protected final JLabel elementType;
	protected final JLabel subType;
	protected final JButton addButton;
	protected final JPanel strategyPanel;

	private final JPanel header;

	public DynamicElement() {
		elementType = new JLabel();
		subType = new JLabel();
		addButton = new JButton("+");
		strategyPanel = new JPanel();
		strategyPanel.setMinimumSize(new Dimension(50, 200));

		header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
		header.add(elementType);
		header.add(Box.createHorizontalGlue());
		header.add(subType);

		final JPanel footer = new JPanel();
		footer.setLayout(new BorderLayout());
		final JPanel rightFooter = new JPanel();
		rightFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));
		footer.add(rightFooter);

		rightFooter.add(addButton, BorderLayout.SOUTH);

		footer.setBackground(ColorPalette.TRANSPARENT);
		rightFooter.setBackground(ColorPalette.TRANSPARENT);

		this.add(header);
		this.add(strategyPanel);
		this.add(footer);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(RuleColors.CONDITION_COLOR);
	}

	protected void setColors(Color strategyPanelColor, Color headerColor, Color backgroundColor, Color borderColor) {
		strategyPanel.setBackground(strategyPanelColor);
		header.setBackground(headerColor);
		this.setBackground(backgroundColor);
		this.setBorder(BorderFactory.createLineBorder(borderColor, 1));
	}

	public void setStrategyElements(Iterable<Component> components) {
		for (Component c : components) {
			strategyPanel.add(c);
		}

		revalidate();
		repaint();
	}
}
