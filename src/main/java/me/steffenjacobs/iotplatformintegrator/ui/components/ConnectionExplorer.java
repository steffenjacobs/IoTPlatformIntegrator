package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedServerConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerConnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerDisconnectedEvent;

/** @author Steffen Jacobs */
public class ConnectionExplorer extends JPanel {
	private static final long serialVersionUID = 2856792428174419970L;

	private final DefaultTreeModel model;

	private final Map<DefaultMutableTreeNode, ServerConnection> nodeTable = new HashMap<>();
	private final Map<ServerConnection, DefaultMutableTreeNode> nodeTableBackwards = new HashMap<>();

	public ConnectionExplorer() {
		super();
		this.setLayout(new BorderLayout());

		final DefaultMutableTreeNode helpTextNode = new DefaultMutableTreeNode("Not connected to any server.", false);

		this.model = new DefaultTreeModel(new DefaultMutableTreeNode("Connections", true)) {
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

		JTree tree = new ServerConnectionTree(this.model);
		tree.setExpandsSelectedPaths(true);

		tree.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (nodeTable.containsKey(node)) {
				EventBus.getInstance().fireEvent(new SelectedServerConnectionChangeEvent(nodeTable.get(node)));
			}
		});

		model.nodeStructureChanged((TreeNode) model.getRoot());

		super.add(tree, BorderLayout.CENTER);

		EventBus.getInstance().addEventHandler(EventType.ServerDisconnected, e -> removeServerConnection(((ServerDisconnectedEvent) e).getServerConnection()));
		EventBus.getInstance().addEventHandler(EventType.ServerConnected, e -> addServerConnection(((ServerConnectedEvent) e).getServerConnection()));
	}

	private void addServerConnection(ServerConnection connection) {

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
		nodeTableBackwards.put(connection, node);

		model.nodeStructureChanged(root);
	}

	private void removeServerConnection(ServerConnection serverConnection) {
		nodeTable.remove(nodeTableBackwards.remove(serverConnection));
	}

	private final class ServerConnectionTree extends JTree implements ActionListener {
		private static final long serialVersionUID = 2462886798062875440L;
		JPopupMenu popup = new JPopupMenu();

		ServerConnectionTree(TreeModel dmtn) {
			super(dmtn);
			JMenuItem mi = new JMenuItem("Disconnect");
			mi.addActionListener(this);
			mi.setActionCommand("disconnect");
			popup.add(mi);

			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {

						// select this row
						ServerConnectionTree.this.setSelectionRow(ServerConnectionTree.this.getClosestRowForLocation(e.getX(), e.getY()));

						if (nodeTable.containsKey(ServerConnectionTree.this.getSelectionPath().getLastPathComponent())) {
							popup.show((JComponent) e.getSource(), e.getX(), e.getY());
						}
					}
				}
			});

		}

		public void actionPerformed(ActionEvent ae) {

			if (ae.getActionCommand().equals("disconnect")) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getSelectionPath().getLastPathComponent();
				selectedNode.removeAllChildren();

				// remove from parent
				DefaultMutableTreeNode parentOfSelectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
				int nodeIndex = parentOfSelectedNode.getIndex(selectedNode);
				parentOfSelectedNode.remove(nodeIndex);

				// update tree at parent node
				((DefaultTreeModel) this.getModel()).nodeStructureChanged((TreeNode) selectedNode);
				EventBus.getInstance().fireEvent(new ServerDisconnectedEvent(nodeTable.get(selectedNode)));

			}
		}
	}
}
