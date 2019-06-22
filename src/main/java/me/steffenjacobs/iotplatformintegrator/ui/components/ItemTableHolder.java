package me.steffenjacobs.iotplatformintegrator.ui.components;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SourceConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;

/** @author Steffen Jacobs */
public class ItemTableHolder {

	public static enum ItemTableHolderType {
		Default, Source, Target
	}

	private final JTable itemsTable;

	public ItemTableHolder(ItemTableHolderType type) {
		itemsTable = createItemsTable();

		switch (type) {
		case Default:
			EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
				updateItemsTable(((SelectedServerConnectionChangeEvent) e).getServerConnection());
			});
			break;
		case Source:
			EventBus.getInstance().addEventHandler(EventType.SourceConnectionChanged, e -> {
				updateItemsTable(((SourceConnectionChangeEvent) e).getServerConnection());
			});
			break;
		case Target:
			EventBus.getInstance().addEventHandler(EventType.TargetConnectionChanged, e -> {
				updateItemsTable(((TargetConnectionChangeEvent) e).getServerConnection());
			});
			break;
		}
	}

	public JTable getItemsTable() {
		return itemsTable;
	}

	private JTable createItemsTable() {

		// create table model
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(3);

		// setup columns
		String[] columnNames = { "Name", "Label", "Type" };
		tableModel.setColumnIdentifiers(columnNames);

		// create JTable
		JTable table = new JTable(tableModel);
		table.setBounds(30, 40, 200, 300);

		return table;
	}

	private void updateItemsTable(ServerConnection serverConnection) {
		DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
		tableModel.setNumRows(0);
		if (serverConnection == null) {
		} else {
			for (SharedItem item : serverConnection.getItemDirectory().getAllItems()) {
				String[] arr = new String[3];
				arr[0] = item.getName();
				arr[1] = item.getLabel();
				arr[2] = "" + item.getType();
				tableModel.addRow(arr);
			}
			tableModel.fireTableDataChanged();

		}

	}

}
