package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.swing.JComboBox;

import org.apache.commons.lang3.ArrayUtils;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.RuleRelatedAnnotation;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class RuleElementRecommender {

	private final RuleComponentRegistry registry;
	private ItemDirectory targetItemDirectory = null;

	public RuleElementRecommender(RuleComponentRegistry registry) {
		this.registry = registry;

		EventBus.getInstance().addEventHandler(EventType.TARGET_CONNECTION_CHANGE, e -> {
			ServerConnection c = ((TargetConnectionChangeEvent) e).getServerConnection();
			if (c != null) {
				targetItemDirectory = c.getItemDirectory();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void addRecommendations(Collection<Component> strategyElements) {
		Optional<Component> cOperator = getType(strategyElements, "operator");
		Optional<Component> cItem = getType(strategyElements, "itemName");
		Optional<Component> cState = getType(strategyElements, "state");
		Optional<Component> cCommand = getType(strategyElements, "command");

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

			// add recommendations for item for created null item
			if (item.isPresent() && item.get().getType() == ItemType.Unknown) {
				ItemDirectory currentItemDirectory;
				if (targetItemDirectory == null) {
					currentItemDirectory = App.getDatabaseConnectionObject().getItemDirectory();
				} else {
					currentItemDirectory = targetItemDirectory;
				}
				
				//add only those items with correct command or operator
				for (SharedItem si : currentItemDirectory.getAllItems()) {
					if (si.getType() == ItemType.Unknown) {
						continue;
					}
					if (cCommand.isPresent()) {
						if (!ArrayUtils.contains(si.getType().getAllowedCommands(), ((JComboBox<?>) cCommand.get()).getSelectedItem())) {
							continue;
						}
					}
					if (cOperator.isPresent()) {
						if (!ArrayUtils.contains(si.getType().getDatatype().getOperations(), ((JComboBox<?>) cOperator.get()).getSelectedItem())) {
							continue;
						}
					}
					JComboBox<SharedItem> box = (JComboBox<SharedItem>) cItem.get();
					if (!containsItem(box, si)) {
						box.addItem(si);
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
