package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RuleController;

/** @author Steffen Jacobs */
public class RuleTableHolder {

	private final JTable rulesTable;
	private final RuleController ruleController = new RuleController();

	public RuleTableHolder() {
		rulesTable = createRulesTable();
		setupRulesTable();

		EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
			refreshRulesTable(((SelectedServerConnectionChangeEvent) e).getServerConnection());
		});

	}

	private void setupRulesTable() {
		rulesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesTable.getSelectionModel().addListSelectionListener(e -> {
			SharedRule rule = ruleController.getRuleByIndex(rulesTable.getSelectedRow());
			if (ruleController.getLastSelectedRule() != rule) {
				EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(rule));
			}
		});
	}

	private void refreshRulesTable(ServerConnection serverConnection) {
		rulesTable.getSelectionModel().clearSelection();
		if (serverConnection == null) {
			updateRuleTable(rulesTable, new ArrayList<>());
		} else {
			updateRuleTable(rulesTable, serverConnection.getRules());
		}
		EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(null));
	}

	private void updateRuleTable(JTable rulesTable, Iterable<SharedRule> rules) {
		DefaultTableModel tableModel = (DefaultTableModel) rulesTable.getModel();

		tableModel.setNumRows(0);
		for (SharedRule rule : rules) {
			String[] arr = new String[3];
			arr[0] = rule.getName();
			arr[1] = rule.getId();
			arr[2] = rule.getVisible();
			tableModel.addRow(arr);
		}
		tableModel.fireTableDataChanged();
	}

	private JTable createRulesTable() {

		// create table model
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(3);

		// setup columns
		String[] columnNames = { "Name", "UUID", "visible" };
		tableModel.setColumnIdentifiers(columnNames);

		// create JTable
		JTable table = new JTable(tableModel);
		table.setBounds(30, 40, 200, 300);

		return table;
	}

	public JTable getRulesTable() {
		return rulesTable;
	}
}
