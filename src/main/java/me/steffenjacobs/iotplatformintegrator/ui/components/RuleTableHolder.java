package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedTargetRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SourceConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RuleController;

/** @author Steffen Jacobs */
public class RuleTableHolder {

	public static enum RuleTableHolderType {
		Default, Source, Target, Remote
	}

	private final JTable rulesTable;
	private final RuleController ruleController;
	private final RuleTableHolderType type;
	private Iterable<SharedRule> lastRules = new ArrayList<>();

	public RuleTableHolder(RuleTableHolderType type) {
		this(type, null);
	}
	
	public RuleTableHolder(RuleTableHolderType type, ServerConnection databaseConnection) {
		ruleController = new RuleController(type);
		if(databaseConnection != null) {
			ruleController.setMockConnection(databaseConnection);
		}
				
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
		case Remote:
			EventBus.getInstance().addEventHandler(EventType.RemoteRuleAdded, e -> {
				((ArrayList<SharedRule>) lastRules).add(((RemoteRuleAddedEvent) e).getSelectedRule());
				updateRuleTable(rulesTable, lastRules);
			});
			EventBus.getInstance().addEventHandler(EventType.ClearAllRemoteRules, e -> {
				lastRules = new ArrayList<>();
				updateRuleTable(rulesTable, lastRules);
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
					EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(rule));
				}
				break;
			case Source:
				EventBus.getInstance().fireEvent(new SelectedSourceRuleChangeEvent(rule));
				break;
			case Target:
				EventBus.getInstance().fireEvent(new SelectedTargetRuleChangeEvent(rule));
				break;
			case Remote:
				EventBus.getInstance().fireEvent(new RemoteRuleChangeEvent(rule));			
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
			EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(null));
			break;
		case Source:
			EventBus.getInstance().fireEvent(new SelectedSourceRuleChangeEvent(null));		
			break;
		case Target:
			EventBus.getInstance().fireEvent(new SelectedTargetRuleChangeEvent(null));		
			break;
		case Remote:
			EventBus.getInstance().fireEvent(new RemoteRuleChangeEvent(null));				
			break;	
		}
	}

	private void updateRuleTable(JTable rulesTable, Iterable<SharedRule> rules) {
		this.lastRules = rules;
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
		DefaultTableModel tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 7112329891318711679L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
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
