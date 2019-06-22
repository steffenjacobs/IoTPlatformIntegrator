package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementRemovedEvent;

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
	protected final JButton removeButton;
	protected final JPanel strategyPanel;

	private final JPanel header;

	public DynamicElement(ElementType type, UUID uuid) {
		elementType = new JLabel(type.getDisplayString());
		elementType.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JPopupMenu popupElement = new JPopupMenu();
		for(ElementType t : ElementType.values()) {
			JMenuItem elementTypeTrigger = new JMenuItem(t.getDisplayString());
			popupElement.add(elementTypeTrigger);
		}
		this.add(popupElement);
		setHeaderStyle(elementType, popupElement);
		
		subType = new JLabel();
		subType.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JPopupMenu popupSubType = new JPopupMenu();
		if(type == ElementType.Trigger) {
		}
		this.add(popupSubType);
		setHeaderStyle(subType, popupSubType);

		addButton = new JButton("+");
		removeButton = new JButton("-");
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

		rightFooter.add(removeButton, BorderLayout.SOUTH);
		rightFooter.add(addButton, BorderLayout.SOUTH);

		footer.setBackground(ColorPalette.TRANSPARENT);
		rightFooter.setBackground(ColorPalette.TRANSPARENT);

		this.add(header);
		this.add(strategyPanel);
		this.add(footer);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addButton.addActionListener(e -> EventBus.getInstance().fireEvent(new RuleElementAddedEvent(type, uuid)));
		removeButton.addActionListener(e -> EventBus.getInstance().fireEvent(new RuleElementRemovedEvent(type, uuid)));
	}

	private void setHeaderStyle(JComponent component, JPopupMenu popup) {
		component.setFont(Style.FONT_UI_HEADER);
		component.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				component.setForeground(ColorPalette.FONT_COLOR_SELECTION);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				component.setForeground(ColorPalette.FONT_COLOR);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				popup.show((JComponent) e.getSource(), e.getX(), e.getY());
			}
		});
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
