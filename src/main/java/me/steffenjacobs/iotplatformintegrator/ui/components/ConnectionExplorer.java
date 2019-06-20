package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;

/** @author Steffen Jacobs */
public class ConnectionExplorer extends JPanel {
	private static final long serialVersionUID = 2856792428174419970L;

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Connections");

	private final Map<ServerConnection, DefaultMutableTreeNode> nodeTable = new HashMap<>();

	public ConnectionExplorer() {
		super();
		this.setLayout(new BorderLayout());

		JTree tree = new JTree(root);
		tree.setExpandsSelectedPaths(true);
		super.add(tree, BorderLayout.CENTER);
	}

	public void addConnection(ServerConnection connection) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(connection.getInstanceName(), true);
		DefaultMutableTreeNode platformNode = new DefaultMutableTreeNode("Platform Type: " + connection.getPlatformType(), false);
		DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode("Version: " + connection.getVersion(), false);
		DefaultMutableTreeNode urlNode = new DefaultMutableTreeNode("URL: " + connection.getUrl(), false);
		DefaultMutableTreeNode portNode = new DefaultMutableTreeNode("Port: " + connection.getPort(), false);

		node.add(platformNode);
		node.add(versionNode);
		node.add(urlNode);
		node.add(portNode);

		root.add(node);
		nodeTable.put(connection, node);

		this.repaint();
		this.revalidate();
	}

}
