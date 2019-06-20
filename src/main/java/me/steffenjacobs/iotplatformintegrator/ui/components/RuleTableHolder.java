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
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedTargetRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SourceConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RuleController;

/** @author Steffen Jacobs */
public class RuleTableHolder {

	public static enum RuleTableHolderType {
		Default, Source, Target
	}

	private final JTable rulesTable;
	private final RuleController ruleController = new RuleController();
	private final RuleTableHolderType type;

	public RuleTableHolder(RuleTableHolderType type) {
		this.type = type;
		rulesTable = createRulesTable();
		setupRulesTable();

		switch (type) {
		case Default:
			EventBus.getInstance().addEventHandler(EventType.SelectedServerConnectionChanged, e -> {
				refreshRulesTable(((SelectedServerConnectionChangeEvent) e).getServerConnection());
			});
			break;
		case Source:
			EventBus.getInstance().addEventHandler(EventType.SourceConnectionChanged, e -> {
				refreshRulesTable(((SourceConnectionChangeEvent) e).getServerConnection());
			});
			break;
		case Target:
			EventBus.getInstance().addEventHandler(EventType.TargetConnectionChanged, e -> {
				refreshRulesTable(((TargetConnectionChangeEvent) e).getServerConnection());
			});
			break;
		}

	}

	private void setupRulesTable() {
		rulesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rulesTable.getSelectionModel().addListSelectionListener(e -> {
			SharedRule rule = ruleController.getRuleByIndex(rulesTable.getSelectedRow());

			switch (type) {
			case Default:
				if (ruleController.getLastSelectedRule() != rule) {
					EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(rule));
				}
				break;
			case Source:
				EventBus.getInstance().fireEvent(new SelectedSourceRuleChangeEvent(rule));
				break;
			case Target:
				EventBus.getInstance().fireEvent(new SelectedTargetRuleChangeEvent(rule));
				break;
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
		switch (type) {
		case Default:
			EventBus.getInstance().fireEvent(new SelectedRuleChangedEvent(null));
			break;
		case Source:
			EventBus.getInstance().fireEvent(new SelectedSourceRuleChangeEvent(null));
		case Target:
			EventBus.getInstance().fireEvent(new SelectedTargetRuleChangeEvent(null));
		}
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
