package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.StateType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedItemChangedEvent;

/** @author Steffen Jacobs */
public class ItemDetailsPanel extends JPanel {
	private static final long serialVersionUID = -3955999116242608088L;

	private final JTextField txtName;
	private final JTextField txtLabel;
	private final JTextField txtType;
	private final JTextField txtApplicableCommands;
	private final JTextField txtApplicableStates;
	private final JTextField txtDataType;

	private final JPanel noItemSelected, itemSelected;

	@SuppressWarnings("unused")
	private SharedItem item = null;

	public ItemDetailsPanel() {
		super();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Name: ", form);
		txtName = new JTextField();
		formUtility.addLastField(txtName, form);

		formUtility.addLabel("Label: ", form);
		txtLabel = new JTextField();
		formUtility.addLastField(txtLabel, form);

		formUtility.addLabel("Item Type: ", form);
		txtType = new JTextField();
		formUtility.addLastField(txtType, form);

		formUtility.addLabel("Applicable Commands: ", form);
		txtApplicableCommands = new JTextField();
		formUtility.addLastField(txtApplicableCommands, form);

		formUtility.addLabel("Applicable States: ", form);
		txtApplicableStates = new JTextField();
		formUtility.addLastField(txtApplicableStates, form);

		formUtility.addLabel("Data Type: ", form);
		txtDataType = new JTextField();
		formUtility.addLastField(txtDataType, form);

		form.setBorder(new EmptyBorder(2, 2, 2, 2));

		// Add form panel to panel
		itemSelected = new JPanel();
		itemSelected.add(form);

		itemSelected.setBorder(BorderFactory.createTitledBorder("Item Details"));
		itemSelected.setLayout(new BoxLayout(itemSelected, BoxLayout.Y_AXIS));

		noItemSelected = new JPanel();
		noItemSelected.add(new JLabel("Please select an item to display the details.                                                                          "));

		super.add(noItemSelected, BorderLayout.NORTH);

		EventBus.getInstance().addEventHandler(EventType.SELECTED_ITEM_CHANGE, e -> setDisplayedItem(((SelectedItemChangedEvent) e).getItem()));
	}

	private void setDisplayedItem(SharedItem item) {
		this.item = item;
		if (item == null) {
			super.removeAll();
			super.add(noItemSelected, BorderLayout.NORTH);
		} else {
			txtName.setText(item.getName());
			txtLabel.setText(item.getLabel());
			txtType.setText(item.getType().name());
			txtApplicableCommands.setText(Arrays.stream(item.getType().getAllowedCommands()).map(Command::name).collect(Collectors.joining(", ")));
			txtApplicableStates.setText(Arrays.stream(item.getType().getAllowedStates()).map(StateType::name).collect(Collectors.joining(", ")));
			txtDataType.setText(item.getType().getDatatype().name());

			super.removeAll();
			super.add(itemSelected, BorderLayout.CENTER);
		}
		this.repaint();
		this.revalidate();
	}

}
