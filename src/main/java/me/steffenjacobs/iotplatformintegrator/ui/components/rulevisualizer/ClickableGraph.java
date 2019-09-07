package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.ui.util.Pair;

public class ClickableGraph implements ViewerListener {

	public static enum SelectionType {
		RULE_FILTER("#8bc4a6"), DIFF_FILTER_COSMETIC("#c4b68b"), DIFF_FILTER_CREATE("#baa380"), DIFF_FILTER_UPDATE("#ba9280"), DIFF_FILTER_DELETE(
				"ba8480"), DIFF_FILTER_DIFF_FULL_CREATED("#9fba80"), DIFF_FILTER_DIFF_FULL_DELETED("#ba8089"), UNKNOWN("#000000");

		private final String color;

		SelectionType(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}
	}

	private static final String COLOR_DIFF_NODE = "#8bb0c4";
	private static final String COLOR_RULE_NODE = "#23729e";
	private static final String COLOR_EDGE = "#c5ccd1";
	private static final String COLOR_DIFF_NODE_SELECTED = "#627782";
	private static final String COLOR_RULE_NODE_SELECTED = "#0b283d";
	private static final String SIZE_RULE_NODE = "15px";
	private static final String SIZE_DIFF_NODE = "8px";
	private static final String SIZE_RULE_NODE_SELECTED = "20px";
	private static final String SIZE_DIFF_NODE_SELECTED = "13px";

	private static final Logger LOG = LoggerFactory.getLogger(ClickableGraph.class);

	private final AtomicBoolean loop = new AtomicBoolean(true);
	private ViewPanel view;
	private ViewerListener listener;
	private Graph graph;

	private final Map<Node, Boolean> nodeWithType = new HashMap<>();
	private Node lastSelectedNode = null;

	private final ReentrantLock lock = new ReentrantLock(true);

	final static boolean enableFineLogging = false;

	private Viewer viewer;

	public ClickableGraph(ViewerListener listener) {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.listener = listener;
		graph = new SingleGraph("RuleNetGraph");

		viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		viewer.enableAutoLayout();
		view = viewer.addDefaultView(false);

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		new Thread(() -> {
			while (loop.get()) {
				try {
					fromViewer.pump();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	public Node createAndAddNode(String nodeId, boolean isDiff) {
		lock.lock();
		Node n = null;
		try {
			n = graph.addNode(nodeId);
			if (isDiff) {
				n.addAttribute("ui.style", "fill-color: " + COLOR_DIFF_NODE + ";");
				n.addAttribute("ui.style", "size: " + SIZE_DIFF_NODE + ";");
			} else {
				n.addAttribute("ui.label", n.getId());
				n.addAttribute("ui.style", "fill-color: " + COLOR_RULE_NODE + ";");
				n.addAttribute("ui.style", "size: " + SIZE_RULE_NODE + ";");
			}
			nodeWithType.put(n, isDiff);
		} catch (IdAlreadyInUseException e) {
			LOG.warn("Could not create node {}: {}", nodeId, e.getMessage());
		}
		lock.unlock();
		return n;
	}

	public ViewPanel getViewPanel() {
		return view;
	}

	public void viewClosed(String id) {
		loop.set(false);
		listener.viewClosed(id);
	}

	public void buttonPushed(String id) {
		listener.buttonPushed(id);
	}

	public void buttonReleased(String id) {
		lock.lock();
		listener.buttonReleased(id);
		lock.unlock();
	}

	public void clear() {
		lock.lock();
		graph.clear();
		nodeWithType.clear();
		lock.unlock();
	}

	public void refreshEdges(Set<Pair<String>> edges) {
		SwingUtilities.invokeLater(() -> {
			lock.lock();
			if (enableFineLogging) {
				LOG.info("Cleared edges.");
				LOG.info("Available nodes: {}", graph.getNodeSet());
			}
			clearEdges();
			for (Pair<String> edge : edges) {
				try {
					Edge e = graph.addEdge(edge.getLeft() + "_" + edge.getRight(), edge.getLeft(), edge.getRight(), true);
					e.addAttribute("ui.style", "fill-color: " + COLOR_EDGE + ";");
				} catch (ElementNotFoundException | IdAlreadyInUseException e) {
					if (enableFineLogging) {
						LOG.warn("Could not find a node for edge {}_{}", edge.getLeft(), edge.getRight());
					}
				}
			}
			viewer.enableAutoLayout();
			lock.unlock();
		});
	}

	private void clearEdges() {
		graph.getEdgeSet().forEach(graph::removeEdge);
	}

	public void selectFilterNode(String id, boolean filtered, Function<String, Node> nodesByUUID, SelectionType type) {
		if(type == SelectionType.UNKNOWN) {
			return;
		}
		lock.lock();
		Node node = nodesByUUID.apply(id);
		// only continue with existing nodes
		if (node != null) {
			if (filtered) {
				node.addAttribute("ui.style", "stroke-mode: none;");
				node.removeAttribute("ui.style");

				node.addAttribute("ui.style", "fill-color: " + type.getColor() + ";");
				node.addAttribute("ui.style", "size: " + SIZE_RULE_NODE + ";");

				lock.unlock();
			} else {
				// reset filter
				node.addAttribute("ui.style", "stroke-mode: none;");
				node.removeAttribute("ui.style");

				if (nodeWithType.get(node)) {
					node.addAttribute("ui.style", "fill-color: " + COLOR_DIFF_NODE + ";");
					node.addAttribute("ui.style", "size: " + SIZE_DIFF_NODE + ";");
				} else {
					node.addAttribute("ui.style", "fill-color: " + COLOR_RULE_NODE + ";");
					node.addAttribute("ui.style", "size: " + SIZE_RULE_NODE + ";");
				}
				lock.unlock();
			}
		} else {
			LOG.error("Could not filter node {}", id);
			lock.unlock();
		}
	}

	public void selectNode(String id, boolean suppressEvents, Map<String, Node> nodesByUUID, Map<String, SharedRule> ruleByUUID) {
		lock.lock();
		// de-select old node if present
		if (lastSelectedNode != null) {
			final Boolean nodeType = nodeWithType.get(lastSelectedNode);
			lastSelectedNode.addAttribute("ui.style", "stroke-mode: none;");
			lastSelectedNode.removeAttribute("ui.style");
			if (nodeType == null) {

			} else if (nodeType == true) {
				lastSelectedNode.addAttribute("ui.style", "fill-color: " + COLOR_DIFF_NODE + ";");
				lastSelectedNode.addAttribute("ui.style", "size: " + SIZE_DIFF_NODE + ";");
			} else if (nodeType == false) {
				lastSelectedNode.addAttribute("ui.style", "fill-color: " + COLOR_RULE_NODE + ";");
				lastSelectedNode.addAttribute("ui.style", "size: " + SIZE_RULE_NODE + ";");
			}
		}

		// select new node
		if (nodesByUUID.containsKey(id)) {
			final Node node = nodesByUUID.get(id);
			final Boolean nodeType = nodeWithType.get(node);

			node.removeAttribute("ui.style");
			if (nodeType == null) {

			} else if (nodeType == true) {
				node.addAttribute("ui.style", "stroke-mode: plain;");
				node.addAttribute("ui.style", "stroke-color: " + COLOR_DIFF_NODE_SELECTED + ";");
				node.addAttribute("ui.style", "stroke-width: 3px;");
				node.addAttribute("ui.style", "size: " + SIZE_DIFF_NODE_SELECTED + ";");
				lastSelectedNode = node;
				lock.unlock();
				if (!suppressEvents) {
					EventBus.getInstance().fireEvent(new SelectedRuleDiffChangeEvent(App.getRuleDiffCache().getRuleDiffParts(id)));
				}

			} else if (nodeType == false) {
				node.addAttribute("ui.style", "size: " + SIZE_RULE_NODE_SELECTED + ";");
				node.addAttribute("ui.style", "stroke-mode: plain;");
				node.addAttribute("ui.style", "stroke-color: " + COLOR_RULE_NODE_SELECTED + ";");
				node.addAttribute("ui.style", "stroke-width: 3px;");
				lastSelectedNode = node;
				lock.unlock();
				if (!suppressEvents) {
					EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(ruleByUUID.get(id)));
				}
			} else {
				lock.unlock();
			}
		} else {
			lock.unlock();
		}
	}

	public Node getLastSelectedNode() {
		return lastSelectedNode;
	}
}
