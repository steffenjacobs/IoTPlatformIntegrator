package me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCopiedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementCreatedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementDeletedEvent;

/** @author Steffen Jacobs */

public abstract class DynamicElement extends JPanel {
	private static final long serialVersionUID = 162937647608306926L;

	public enum ElementType {
		TRIGGER("TRIGGER"), CONDITION("CONDITION"), ACTION("ACTION");

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
		subType = new JLabel();

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

		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				onAddButtonClicked(e, uuid, type);
			}
		});
		removeButton.addActionListener(e -> EventBus.getInstance().fireEvent(new RuleElementDeletedEvent(type, uuid)));
	}

	private void onAddButtonClicked(MouseEvent e, UUID uuidForCopy, ElementType elementType) {
		final JPopupMenu selectionPopup = new JPopupMenu();

		final JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(l -> {
			EventBus.getInstance().fireEvent(new RuleElementCopiedEvent(elementType, uuidForCopy));
			selectionPopup.setVisible(false);
		});
		selectionPopup.add(copy);

		selectionPopup.addSeparator();

		selectionPopup.add(new JLabel("Triggers"));
		for (TriggerType triggerType : TriggerType.acceptableValues()) {
			final JMenuItem menu = new JMenuItem(triggerType.name());
			menu.addActionListener(l -> fireCreationEvent(ElementType.TRIGGER, triggerType, selectionPopup));
			selectionPopup.add(menu);
		}

		selectionPopup.addSeparator();
		selectionPopup.add(new JLabel("Conditions"));
		for (ConditionType conditionType : ConditionType.acceptableValues()) {
			final JMenuItem menu = new JMenuItem(conditionType.name());
			menu.addActionListener(l -> fireCreationEvent(ElementType.CONDITION, conditionType, selectionPopup));
			selectionPopup.add(menu);
		}

		selectionPopup.addSeparator();
		selectionPopup.add(new JLabel("Actions"));
		for (ActionType actionType : ActionType.acceptableValues()) {
			final JMenuItem menu = new JMenuItem(actionType.name());
			menu.addActionListener(l -> fireCreationEvent(ElementType.ACTION, actionType, selectionPopup));
			selectionPopup.add(menu);
		}

		final Point p = ((Component) e.getSource()).getLocationOnScreen();
		selectionPopup.setVisible(true);
		selectionPopup.setLocation(p.x + e.getX(), p.y + e.getY());
	}

	private void fireCreationEvent(ElementType elementType, SharedElementType sharedElementType, JPopupMenu popupToClose) {
		EventBus.getInstance().fireEvent(new RuleElementCreatedEvent(null, elementType, sharedElementType));
		popupToClose.setVisible(false);
	}

	protected void setColors(Color strategyPanelColor, Color headerColor, Color backgroundColor, Color borderColor) {
		strategyPanel.setBackground(strategyPanelColor);
		header.setBackground(headerColor);
		this.setBackground(backgroundColor);
		this.setBorder(BorderFactory.createLineBorder(borderColor, 1));
	}

	public void setStrategyElements(Iterable<Component> components) {
		for (Component c : components) {
			if (c instanceof JTextField || c instanceof JComboBox<?>) {
				c.setMinimumSize(new Dimension(150, 25));
				c.setPreferredSize(new Dimension(150, 25));
			}
			strategyPanel.add(c);
		}

		revalidate();
		repaint();
	}

	public Component[] getStrategyElements() {
		return strategyPanel.getComponents();
	}
}
