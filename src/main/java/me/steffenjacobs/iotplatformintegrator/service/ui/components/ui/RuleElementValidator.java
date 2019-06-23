package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.RuleRelatedAnnotation;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.StateType;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class RuleElementValidator {

	private static final Logger LOG = LoggerFactory.getLogger(RuleElementValidator.class);

	private final RuleComponentRegistry registry;
	private ItemDirectory targetItemDirectory = null;

	public RuleElementValidator(RuleComponentRegistry registry) {
		this.registry = registry;

		EventBus.getInstance().addEventHandler(EventType.TargetConnectionChanged, e -> {
			ServerConnection c = ((TargetConnectionChangeEvent) e).getServerConnection();
			if (c != null) {
				targetItemDirectory = c.getItemDirectory();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void addValidation(Collection<Component> strategyElements) {
		Optional<Component> cOperator = getType(strategyElements, "operator");
		Optional<Component> cItem = getType(strategyElements, "itemName");
		Optional<Component> cState = getType(strategyElements, "state");
		Optional<Component> cCommand = getType(strategyElements, "command");

		if (cState.isPresent() && cItem.isPresent()) {
			final Optional<SharedItem> item;
			if (cItem.get() instanceof JComboBox<?>) {
				SharedItem si = (SharedItem) ((JComboBox<?>) cItem.get()).getSelectedItem();
				if (si != null) {
					item = Optional.of(si);
				} else {
					item = Optional.empty();
				}
			} else {
				item = Optional.empty();
			}

			if (item.isPresent()) {
				if (cState.get() instanceof JTextField) {
					JTextField txt = (JTextField) cState.get();
					txt.addKeyListener(new KeyAdapter() {

						@Override
						public void keyReleased(KeyEvent e) {
							for (StateType stateType : item.get().getType().getAllowedStates()) {
								switch (stateType) {
								case Boolean:
									if ("true".equals(txt.getText().toLowerCase()) || "false".equals(txt.getText().toLowerCase()) || "t".equals(txt.getText().toLowerCase())
											|| "f".equals(txt.getText().toLowerCase()) || "1".equals(txt.getText().toLowerCase()) || "0".equals(txt.getText().toLowerCase())) {
										txt.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
										txt.setToolTipText("Validation successful");
									} else {
										txt.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
										txt.setToolTipText("Value needs to be an Boolean.");
									}
									break;
								case Integer: {
									try {
										Integer.parseInt(txt.getText());
										txt.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
										txt.setToolTipText("Validation successful");
									} catch (NumberFormatException ex) {
										txt.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
										txt.setToolTipText("Value needs to be an Integer.");
									}
									break;
								}
								case Decimal:
									try {
										Double.parseDouble(txt.getText());
										txt.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
										txt.setToolTipText("Validation successful");
									} catch (NullPointerException | NumberFormatException ex) {
										txt.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
										txt.setToolTipText("Value needs to be a decimal number.");
									}
									break;
								case Command:
									Command parsedCommand = Command.parse(txt.getText());
									if (parsedCommand == Command.Unknown) {
										txt.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
										txt.setToolTipText(String.format("Unknown command: %s", txt.getText()));
										break;
									}
									boolean found = false;
									for (Command command : item.get().getType().getAllowedCommands()) {
										if (command == parsedCommand) {
											txt.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
											txt.setToolTipText("Validation successful");
											found = true;
											break;

										}
									}
									if (found) {
										break;
									}
									txt.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
									Collection<String> commandStrings = new ArrayList<String>();
									for (Command c : item.get().getType().getAllowedCommands()) {
										commandStrings.add(c.name());
									}
									txt.setToolTipText(String.format("Invalid command %s for item of type %s, allowed commands: %s", parsedCommand.name(),
											item.get().getType().name(), commandStrings));
									break;
								case String:
								case Object:
									// nothing to do
									break;
								default:
									LOG.error("Unexpected state type {} for item {}", stateType.name(), item.get().getName());
								}
							}
						}
					});
				} else {
					LOG.error("Unexpected type for state field: {}", cState.get().getClass().getName());
				}
			}
		}

		if (cItem.isPresent()) {
			final Optional<SharedItem> item;
			if (cItem.get() instanceof JComboBox<?>) {
				SharedItem si = (SharedItem) ((JComboBox<?>) cItem.get()).getSelectedItem();
				if (si != null) {
					item = Optional.of(si);
				} else {
					item = Optional.empty();
				}
			} else {
				item = Optional.empty();
			}

			// add recommendations for commands based on current item
			if (item.isPresent() && cCommand.isPresent()) {
				for (Command command : item.get().getType().getAllowedCommands()) {
					JComboBox<Command> box = (JComboBox<Command>) cCommand.get();
					if (!containsItem(box, command)) {
						box.addItem(command);
					}
				}
			}
			if (cState.isPresent()) {
				// TODO: validation if state is a command or item
			}

			// add recommendations for operator based on current item
			if (item.isPresent() && cOperator.isPresent()) {
				for (Operation op : item.get().getType().getDatatype().getOperations()) {
					JComboBox<Operation> box = (JComboBox<Operation>) cOperator.get();
					if (!containsItem(box, op)) {
						box.addItem(op);
					}
				}
			}
			// add recommendations for item based on operator and/or command
			if (targetItemDirectory != null && cItem.get() instanceof JComboBox<?>) {
				Collection<SharedItem> items = new ArrayList<>();
				if (cOperator.isPresent() && cCommand.isPresent()) {
					for (SharedItem si : targetItemDirectory.getAllItems()) {
						if (ArrayUtils.contains(si.getType().getAllowedCommands(), ((JComboBox<?>) cCommand.get()).getSelectedItem())
								&& ArrayUtils.contains(si.getType().getDatatype().getOperations(), ((JComboBox<?>) cOperator.get()).getSelectedItem())) {
							items.add(si);
						}
					}
				} else if (cOperator.isPresent()) {
					for (SharedItem si : targetItemDirectory.getAllItems()) {
						if (ArrayUtils.contains(si.getType().getDatatype().getOperations(), ((JComboBox<?>) cOperator.get()).getSelectedItem())) {
							items.add(si);
						}
					}
				} else if (cCommand.isPresent()) {
					for (SharedItem si : targetItemDirectory.getAllItems()) {
						if (ArrayUtils.contains(si.getType().getAllowedCommands(), ((JComboBox<?>) cCommand.get()).getSelectedItem())) {
							items.add(si);
						}
					}
				}

				// add alternative items to combobox
				for (SharedItem si : items) {
					JComboBox<SharedItem> box = (JComboBox<SharedItem>) cItem.get();
					if (!containsItem(box, si)) {
						box.addItem(si);
					}
				}
			}
		}

	}

	private <T> boolean containsItem(JComboBox<T> box, T t) {
		for (int i = 0; i < box.getItemCount(); i++) {
			if (box.getItemAt(i) == t) {
				return true;
			}
		}
		return false;
	}

	private Optional<Component> getType(Collection<Component> components, String keyString) {
		for (Component comp : components) {
			RuleRelatedAnnotation annotationFromComponent = registry.getAnnotationFromComponent(comp);
			if (annotationFromComponent != null && annotationFromComponent.getRuleElementSpecificKey() != null
					&& keyString.equals(annotationFromComponent.getRuleElementSpecificKey().getKeyString())) {
				return Optional.of(comp);
			}
		}
		return Optional.empty();
	}
}
