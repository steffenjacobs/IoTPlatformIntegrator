package me.steffenjacobs.iotplatformintegrator.service.manage.render;

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
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectoryHolder;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.PseudocodeEditorController.Token.TokenType;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.RuleComponentRegistry;

/** @author Steffen Jacobs */
public class VisualRenderingStrategy implements RenderingStrategy<Component> {

	private final RuleComponentRegistry registry;

	public VisualRenderingStrategy(RuleComponentRegistry registry) {
		this.registry = registry;

	}

	@Override
	public Component operationComponent(Operation operation, SharedTypeSpecificKey key) {
		DefaultComboBoxModel<Operation> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Operation> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Operation op : Operation.getKnownSubstitutes(operation)) {
			itemModel.addElement(op);
			// TODO: add alternative operations allowed in this item context
		}
		chooseItem.setSelectedItem(operation);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.OPERATOR, operation.name(), operation.name()));
		registry.addAnnotatedComponent(chooseItem, key, -1);
		return chooseItem;
	}

	@Override
	public Component commandComponent(Command command, SharedTypeSpecificKey key) {
		DefaultComboBoxModel<Command> itemModel = new DefaultComboBoxModel<>();
		JComboBox<Command> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.name() : ""));
		for (Command cmd : Command.getKnownSubstitutes(command)) {
			itemModel.addElement(cmd);
			// TODO: add alternative commands allowed by item
		}
		chooseItem.setSelectedItem(command);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s)", TokenType.COMMAND, command.name(), command.name()));
		registry.addAnnotatedComponent(chooseItem, key, -1);
		return chooseItem;
	}

	@Override
	public List<Component> valueComponent(String value, SharedTypeSpecificKey key) {
		// TODO: datatypes
		JTextField txt = new JTextField();
		txt.setText(value);
		registry.addAnnotatedComponent(txt, key, -1);
		return Arrays.asList(txt);
	}

	@Override
	public Component textComponent(String text, String description) {
		JLabel label = new JLabel(text);
		label.setToolTipText(description);
		registry.addAnnotatedComponent(label, null, -1);
		return label;
	}

	@Override
	public Component itemComponent(SharedItem item, SharedTypeSpecificKey key) {
		DefaultComboBoxModel<SharedItem> itemModel = new DefaultComboBoxModel<>();
		JComboBox<SharedItem> chooseItem = new JComboBox<>(itemModel);
		chooseItem.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.getName() : ""));
		itemModel.addElement(item);
		chooseItem.setSelectedItem(item);
		chooseItem.setToolTipText(String.format("Type: %s, Name: %s (%s), Source: %s", TokenType.ITEM, item.getName(), item.getLabel(),
				ItemDirectoryHolder.getInstance().getServerConnection(item).getInstanceName()));
		registry.addAnnotatedComponent(chooseItem, key, -1);
		return chooseItem;
	}

	@Override
	public Component textComponent(String text, String description, TokenType type) {
		return textComponent(text, description);
	}
}
