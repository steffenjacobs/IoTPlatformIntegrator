package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

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
	private static final Logger LOG = LoggerFactory.getLogger(ClickableGraph.class);

	private final AtomicBoolean loop = new AtomicBoolean(true);
	private ViewPanel view;
	private ViewerListener listener;
	private Graph graph;

	private final Map<Node, Boolean> nodeWithType = new HashMap<>();
	private Node lastSelectedNode = null;

	private final ReentrantLock lock = new ReentrantLock(true);

	final static boolean enableFineLogging = false;

	public ClickableGraph(ViewerListener listener) {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.listener = listener;
		graph = new SingleGraph("RuleNetGraph");

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		viewer.enableAutoLayout();
		view = viewer.addDefaultView(false);

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		new Thread(() -> {
			while (loop.get()) {
				lock.lock();
				try {
					fromViewer.pump();
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
				lock.unlock();
			}
		}).start();
	}

	public Node createAndAddNode(String nodeId, boolean isDiff) {
		lock.lock();
		Node n = null;
		try {
			n = graph.addNode(nodeId);
			if (isDiff) {
				n.addAttribute("ui.style", "fill-color: #8bb0c4;");
				n.addAttribute("ui.style", "size: 8px;");
			} else {
				n.addAttribute("ui.label", n.getId());
				n.addAttribute("ui.style", "fill-color: #23729e;");
				n.addAttribute("ui.style", "size: 15px;");
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
		listener.buttonReleased(id);
	}

	public void clear() {
		lock.lock();
		graph.clear();
		nodeWithType.clear();
		lock.unlock();
	}

	public void refreshEdges(Set<Pair<String>> edges) {
		lock.lock();
		edges = new HashSet<Pair<String>>(edges);
		if (enableFineLogging) {
			LOG.info("Cleared edges.");
			LOG.info("Available nodes: {}", graph.getNodeSet());
		}
		clearEdges();
		for (Pair<String> edge : edges) {
			try {
				Edge e = graph.addEdge(edge.getLeft() + "_" + edge.getRight(), edge.getLeft(), edge.getRight(), true);
				e.addAttribute("ui.style", "fill-color: #c5ccd1;");
			} catch (ElementNotFoundException | IdAlreadyInUseException e) {
				if (enableFineLogging) {
					LOG.warn("Could not find a node for edge {}_{}", edge.getLeft(), edge.getRight());
				}
			}
		}
		lock.unlock();
	}

	private void clearEdges() {
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			graph.removeEdge(i);
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
				lastSelectedNode.addAttribute("ui.style", "fill-color: #8bb0c4;");
				lastSelectedNode.addAttribute("ui.style", "size: 8px;");
			} else if (nodeType == false) {
				lastSelectedNode.addAttribute("ui.style", "fill-color: #23729e;");
				lastSelectedNode.addAttribute("ui.style", "size: 15px;");
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
				node.addAttribute("ui.style", "stroke-color: #627782;");
				node.addAttribute("ui.style", "stroke-width: 3px;");
				node.addAttribute("ui.style", "size: 13px;");
				if (!suppressEvents) {
					EventBus.getInstance().fireEvent(new SelectedRuleDiffChangeEvent(App.getRuleDiffCache().getRuleDiffParts(id)));
				}
				lastSelectedNode = node;

			} else if (nodeType == false) {
				node.addAttribute("ui.style", "size: 20px;");
				node.addAttribute("ui.style", "stroke-mode: plain;");
				node.addAttribute("ui.style", "stroke-color: #0b283d;");
				node.addAttribute("ui.style", "stroke-width: 3px;");
				if (!suppressEvents) {
					EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(ruleByUUID.get(id)));
				}
				lastSelectedNode = node;
			}
		}
		lock.unlock();
	}

	public Node getLastSelectedNode() {
		return lastSelectedNode;
	}
}
