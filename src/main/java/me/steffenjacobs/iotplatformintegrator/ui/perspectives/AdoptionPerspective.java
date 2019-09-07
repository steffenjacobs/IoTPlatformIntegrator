package me.steffenjacobs.iotplatformintegrator.ui.perspectives;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.SingleCDockable;
import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.ui.GlobalComponentHolder;
import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.util.DockableUtil;

/** @author Steffen Jacobs */
public class AdoptionPerspective extends Perspective {

	private final JPanel connectionExplorerPanel = new JPanel();
	private final JPanel platformRulesTablePanel = new JPanel();
	private final JPanel remoteRulesTablePanel = new JPanel();

	@Override
	public void onAppear() {
		ConnectionExplorer connectionExplorer = GlobalComponentHolder.getInstance().getConnectionExplorer();
		connectionExplorerPanel.add(connectionExplorer, BorderLayout.CENTER);

		JTable ruleTableSource = GlobalComponentHolder.getInstance().getRuleTable();
		platformRulesTablePanel.removeAll();
		platformRulesTablePanel.add(new JScrollPane(ruleTableSource), BorderLayout.CENTER);

		JTable ruleTableRemote = GlobalComponentHolder.getInstance().getRemoteRuleTable();
		remoteRulesTablePanel.removeAll();
		remoteRulesTablePanel.add(new JScrollPane(ruleTableRemote), BorderLayout.CENTER);
	}

	public AdoptionPerspective(SettingService settingService) {
		connectionExplorerPanel.setLayout(new BorderLayout());
		platformRulesTablePanel.setLayout(new BorderLayout());
		remoteRulesTablePanel.setLayout(new BorderLayout());
		setupDockingEnvironment(settingService);
	}

	private void setupDockingEnvironment(SettingService settingService) {
		control = new CControl();

		final boolean enableEvaluationFeatures = !"1".equals(settingService.getSetting(SettingKey.DISABLE_EVALUATION_FEATURES));

		// create connection explorer window
		SingleCDockable connectionExplorerWindow = DockableUtil.createDockable("ConnectionExplorer-Window", "ConnectionExplorer", connectionExplorerPanel);
		control.addDockable(connectionExplorerWindow);

		// create platform rule table window
		SingleCDockable ruleTableSourceWindow = DockableUtil.createDockable("RuleTablePlatform-Window", "Rules from Platform", platformRulesTablePanel);
		control.addDockable(ruleTableSourceWindow);

		SingleCDockable ruleNetWindow = null;
		if (enableEvaluationFeatures) {
			// create rule net window
			ruleNetWindow = DockableUtil.createDockable("RuleNet-Window", "Remote Rules Network", App.getRuleGraphManager().getGraphPanel());
			control.addDockable(ruleNetWindow);
		}

		// create remote rules window
		SingleCDockable remoteRulesWindow = null;
		if (enableEvaluationFeatures) {
			remoteRulesWindow = DockableUtil.createDockable("RemoteRules-Window", "Remote Rules", remoteRulesTablePanel);
			control.addDockable(remoteRulesWindow);
		}

		// create rule builder window
		SingleCDockable ruleBuilderWindow = DockableUtil.createDockable("RuleBuilder-Window", "RuleBuilder", new RuleBuilder(settingService));
		control.addDockable(ruleBuilderWindow);

		// configure grid
		CGrid grid = new CGrid(control);

		if (enableEvaluationFeatures) {
			grid.add(0, 0, .5, .7, ruleNetWindow);
			grid.add(.575, .7, .425, .3, remoteRulesWindow);
		}
		grid.add(.5, 0, .5, .7, ruleBuilderWindow);
		grid.add(0, .7, .15, .3, connectionExplorerWindow);
		grid.add(.15, .7, .425, .3, ruleTableSourceWindow);

		control.getContentArea().deploy(grid);
	}
}
