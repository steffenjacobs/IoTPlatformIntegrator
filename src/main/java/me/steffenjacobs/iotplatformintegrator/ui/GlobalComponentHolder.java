package me.steffenjacobs.iotplatformintegrator.ui;

import javax.swing.JTable;

import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.components.ItemTableHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleTableHolder;

/** @author Steffen Jacobs */
public class GlobalComponentHolder {

	private static final GlobalComponentHolder INSTANCE = new GlobalComponentHolder();

	public static GlobalComponentHolder getInstance() {
		return INSTANCE;
	}

	private final ConnectionExplorer connectionExplorer = new ConnectionExplorer();

	private final RuleTableHolder ruleTableHolder = new RuleTableHolder();
	private final ItemTableHolder itemTableHolder = new ItemTableHolder();

	public ConnectionExplorer getConnectionExplorer() {
		return connectionExplorer;
	}

	public JTable getRuleTable() {
		return ruleTableHolder.getRulesTable();
	}

	public JTable getItemTable() {
		return itemTableHolder.getItemsTable();
	}

}
