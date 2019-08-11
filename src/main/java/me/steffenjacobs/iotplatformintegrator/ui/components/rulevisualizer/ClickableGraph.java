package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.concurrent.atomic.AtomicBoolean;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class ClickableGraph implements ViewerListener {
	protected AtomicBoolean loop = new AtomicBoolean(true);
	private ViewPanel view;
	private ViewerListener listener;
	private Graph graph;

	public ClickableGraph(ViewerListener listener) {
		this.listener = listener;
		graph = new SingleGraph("ClickableGraph");
		graph.addNode("test-node");

		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
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

	public Edge createAndAddEdge(String edgeId, Node node1, Node node2) {
		return graph.addEdge(edgeId, node1.getId(), node2.getId());
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
}
