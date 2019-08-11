package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickableGraph implements ViewerListener {
	private static final Logger LOG = LoggerFactory.getLogger(ClickableGraph.class);

	private final AtomicBoolean loop = new AtomicBoolean(true);
	private ViewPanel view;
	private ViewerListener listener;
	private Graph graph;

	public ClickableGraph(ViewerListener listener) {
		this.listener = listener;
		graph = new SingleGraph("ClickableGraph");
		graph.addNode("test-node");

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		viewer.enableAutoLayout();
		view = viewer.addDefaultView(false);

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		// Then we need a loop to do our work and to wait for events.
		// In this loop we will need to call the
		// pump() method before each use of the graph to copy back events
		// that have already occurred in the viewer thread inside
		// our thread.

		new Thread(() -> {
			while (loop.get()) {
				fromViewer.pump(); // or fromViewer.blockingPump(); in the nightly builds

				// here your simulation code.

				// You do not necessarily need to use a loop, this is only an example.
				// as long as you call pump() before using the graph. pump() is non
				// blocking. If you only use the loop to look at event, use blockingPump()
				// to avoid 100% CPU usage. The blockingPump() method is only available from
				// the nightly builds.
			}
		}).start();
	}

	public Node createAndAddNode(String nodeId) {
		return graph.addNode(nodeId);
	}

	public ViewPanel getViewPanel() {
		return view;
	}

	public void viewClosed(String id) {
		loop.set(false);
		listener.viewClosed(id);
	}

	public void buttonPushed(String id) {
		System.out.println("Button pushed on node " + id);
		listener.buttonPushed(id);
	}

	public void buttonReleased(String id) {
		System.out.println("Button released on node " + id);
		listener.buttonReleased(id);
	}

	public void clear() {
		graph.clear();
	}

	public void refreshEdges(Set<Pair<String, String>> edges) {
		LOG.info("Cleared edges.");
		LOG.info("Available nodes: {}", graph.getNodeSet());
		clearEdges();
		for (Pair<String, String> edge : edges) {
			try {
				graph.addEdge(edge.getLeft() + "_" + edge.getRight(), edge.getLeft(), edge.getRight());
			} catch (ElementNotFoundException e) {
				LOG.warn("Could not find a node for edge {}_{}", edge.getLeft(), edge.getRight());
			}
		}
	}

	private void clearEdges() {
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			graph.removeEdge(i);
		}
	}
}
