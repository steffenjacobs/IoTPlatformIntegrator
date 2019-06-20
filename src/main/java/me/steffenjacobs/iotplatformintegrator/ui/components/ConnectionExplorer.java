package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.ui.UiEntrypointController;

/** @author Steffen Jacobs */
public class ConnectionExplorer extends JPanel {
	private static final long serialVersionUID = 2856792428174419970L;

	private final DefaultTreeModel model;

	private final Map<DefaultMutableTreeNode, ServerConnection> nodeTable = new HashMap<>();

	public ConnectionExplorer(final UiEntrypointController controller) {
		super();
		this.setLayout(new BorderLayout());

		JTree tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("Connections", true)));
		this.model = (DefaultTreeModel) tree.getModel();
		tree.setExpandsSelectedPaths(true);

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (nodeTable.get(node) != null) {
					controller.setSelectedServerConnection(nodeTable.get(node));
				}

			}
		});

		super.add(tree, BorderLayout.CENTER);
	}

	public void addConnection(ServerConnection connection) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(connection.getInstanceName(), true);
		DefaultMutableTreeNode platformNode = new DefaultMutableTreeNode("Platform Type: " + connection.getPlatformType(), false);
		DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode("Version: " + connection.getVersion(), false);
		DefaultMutableTreeNode urlNode = new DefaultMutableTreeNode("URL: " + connection.getUrl(), false);
		DefaultMutableTreeNode portNode = new DefaultMutableTreeNode("Port: " + connection.getPort(), false);

		node.add(platformNode);
		node.add(versionNode);
		node.add(urlNode);
		node.add(portNode);

		model.insertNodeInto(node, root, root.getChildCount());
		nodeTable.put(node, connection);

		this.repaint();
		this.revalidate();
	}

}
