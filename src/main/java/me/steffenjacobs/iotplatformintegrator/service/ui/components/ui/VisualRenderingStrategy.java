package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public class VisualRenderingStrategy implements RenderingStrategy<Component> {
	@Override
	public Component operationComponent(Operation operation) {
		DefaultComboBoxModel<Operation> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Operation> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Operation op : Operation.getKnownSubstitutes(operation)) {
			itemModel.addElement(op);
			// TODO: add alternative operations allowed in this item context
		}
		chooseItem.setSelectedItem(operation);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.OPERATOR, operation.name(), operation.name()));
		return chooseItem;
	}

	@Override
	public Component commandComponent(Command command) {
		DefaultComboBoxModel<Command> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Command> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Command cmd : Command.getKnownSubstitutes(command)) {
			itemModel.addElement(cmd);
			// TODO: add alternative commands allowed by item
		}
		chooseItem.setSelectedItem(command);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.COMMAND, command.name(), command.name()));
		return chooseItem;
	}

	@Override
	public List<Component> valueComponent(String value) {
		// TODO: datatypes
		JTextField txt = new JTextField();
		txt.setText(value);
		return Arrays.asList(txt);
	}

	@Override
	public Component textComponent(String text, String description) {
		JLabel label = new JLabel(text);
		label.setToolTipText(description);
		return label;
	}

	@Override
	public Component itemComponent(SharedItem item) {
		DefaultComboBoxModel<SharedItem> itemModel = new DefaultComboBoxModel<>();
		JComboBox<SharedItem> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.getName() : ""));
		itemModel.addElement(item);
		chooseItem.setSelectedItem(item);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.ITEM, item.getName(), item.getLabel()));
		return chooseItem;
	}

	@Override
	public Component textComponent(String text, String description, TokenType type) {
		return textComponent(text, description);
	}
}
