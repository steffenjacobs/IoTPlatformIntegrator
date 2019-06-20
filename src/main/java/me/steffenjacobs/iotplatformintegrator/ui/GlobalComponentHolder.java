package me.steffenjacobs.iotplatformintegrator.ui;

import javax.swing.JTable;

import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.components.InstanceChooserPanel;
import me.steffenjacobs.iotplatformintegrator.ui.components.ItemTableHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleDetailsPanel;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleTableHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.RuleTableHolder.RuleTableHolderType;

/** @author Steffen Jacobs */
public class GlobalComponentHolder {

	private static final GlobalComponentHolder INSTANCE = new GlobalComponentHolder();

	public static GlobalComponentHolder getInstance() {
		return INSTANCE;
	}

	private final ConnectionExplorer connectionExplorer = new ConnectionExplorer();

	private final RuleTableHolder ruleTableHolder = new RuleTableHolder(RuleTableHolderType.Default);
	private final RuleTableHolder ruleTableSourceHolder = new RuleTableHolder(RuleTableHolderType.Source);
	private final RuleTableHolder ruleTableTargetHolder = new RuleTableHolder(RuleTableHolderType.Target);
	private final ItemTableHolder itemTableHolder = new ItemTableHolder();

	private final RuleDetailsPanel ruleDetailsPanel = new RuleDetailsPanel();
	private final InstanceChooserPanel instanceChooser = new InstanceChooserPanel();

	public ConnectionExplorer getConnectionExplorer() {
		return connectionExplorer;
	}

	public JTable getRuleTable() {
		return ruleTableHolder.getRulesTable();
	}

	public JTable getRuleTableSource() {
		return ruleTableSourceHolder.getRulesTable();
	}

	public JTable getRuleTableTarget() {
		return ruleTableTargetHolder.getRulesTable();
	}

	public JTable getItemTable() {
		return itemTableHolder.getItemsTable();
	}

	public RuleDetailsPanel getRuleDetailsPanel() {
		return ruleDetailsPanel;
	}

	public InstanceChooserPanel getInstanceChooser() {
		return instanceChooser;
	}
}
