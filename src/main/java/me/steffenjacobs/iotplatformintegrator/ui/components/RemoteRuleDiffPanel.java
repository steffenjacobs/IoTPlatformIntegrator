package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RemoteRuleController;

/** @author Steffen Jacobs */
public class RemoteRuleDiffPanel extends JPanel {
	private static final long serialVersionUID = 2856792428174419970L;

	private final DefaultTreeModel model;

	private final Map<DefaultMutableTreeNode, SharedRule> nodeTable = new HashMap<>();

	private final RemoteRuleController controller;

	public RemoteRuleDiffPanel(RemoteRuleController controller) {
		super();
		this.controller = controller;
		this.setLayout(new BorderLayout());

		final DefaultMutableTreeNode helpTextNode = new DefaultMutableTreeNode("No rules on remote server or not connected.", false);

		this.model = new DefaultTreeModel(new DefaultMutableTreeNode("Rules", true)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void nodeStructureChanged(TreeNode node) {
				super.nodeStructureChanged(node);
				DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
				if (rootNode.getChildCount() == 0) {
					insertNodeInto(helpTextNode, rootNode, 0);
					super.nodeStructureChanged(node);
				} else {
					if (helpTextNode.getParent() != null) {
						removeNodeFromParent(helpTextNode);
					}
				}
			}
		};

		JTree tree = new JTree(this.model);
		tree.setExpandsSelectedPaths(true);

		tree.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (nodeTable.containsKey(node)) {
				EventBus.getInstance().fireEvent(new RemoteRuleChangeEvent(nodeTable.get(node)));
			}
		});

		model.nodeStructureChanged((TreeNode) model.getRoot());

		super.add(createButtonBar(), BorderLayout.NORTH);
		super.add(tree, BorderLayout.CENTER);

		EventBus.getInstance().addEventHandler(EventType.RemoteRuleAdded, e -> addRule(((RemoteRuleAddedEvent) e).getSelectedRule()));
	}

	private JPanel createButtonBar() {
		JPanel fp = new JPanel();
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(e -> controller.getRules(rule -> addRule(rule)));
		fp.add(refreshButton);
		return fp;
	}

	private void addRule(SharedRule rule) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(String.format("%s (%s)", rule.getName(), rule.getId()), true);
		controller.getDiffs(rule, diff -> {
			DefaultMutableTreeNode diffNode = new DefaultMutableTreeNode(String.format("%s (%s)", diff.getLabel(), diff.getRelativeElementId()), true);
			node.add(diffNode);
			model.nodeStructureChanged(node);
		});

		model.insertNodeInto(node, root, root.getChildCount());
		nodeTable.put(node, rule);
		model.nodeStructureChanged(root);

	}
}
