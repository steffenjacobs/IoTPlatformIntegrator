package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.graphstream.graph.Node;
import org.graphstream.ui.view.ViewerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<String, Node> nodesByUUID = new HashMap<>();

	private final ClickableGraph graph;

	public RuleGraphManager() {
		graph = createVisualization();

		EventBus.getInstance().addEventHandler(EventType.RemoteRuleAdded, e -> visualizeRule(((WithSharedRuleEvent) e).getSelectedRule()));
		EventBus.getInstance().addEventHandler(EventType.ClearAllRemoteRules, e -> clearRules());
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> visualizeRuleDiff(((RuleDiffChangeEvent) e).getDiffElement()));
		EventBus.getInstance().addEventHandler(EventType.RuleDiffAdded, e -> visualizeRuleDiff(((RuleDiffAddedEvent) e).getDiffElement()));
	}

	private ClickableGraph createVisualization() {
		return new ClickableGraph(new ViewerListener() {

			@Override
			public void viewClosed(String viewName) {
				// should not happen
			}

			@Override
			public void buttonReleased(String id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void buttonPushed(String id) {
				// ignore
			}
		});
	}

	private void visualizeRule(SharedRule rule) {
		graph.createAndAddNode(rule.getId());
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement) {
		final Node anchor;
		if (diffElement.getPrevDiff().isPresent()) {
			anchor = nodesByUUID.get(diffElement.getPrevDiff().get().getUid().toString());
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find diff anchor node {}.", diffElement.getPrevDiff().get().getUid().toString());
				return;
			}
		} else {

			if (!diffElement.getSourceRule().isPresent()) {
				createNode(diffElement);
				LOG.warn("Could not find rule source rule for diff {}.", diffElement.getUid().toString());
				return;
			}
			SharedRule sharedRule = diffElement.getSourceRule().get();
			anchor = nodesByUUID.get(sharedRule.getId());
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find source rule node {}.", sharedRule.getId());
				return;
			}
		}

		Node insertedNode = createNode(diffElement);

		graph.createAndAddEdge(anchor.getId() + "-" + insertedNode.getId(), anchor, insertedNode);

	}

	private Node createNode(SharedRuleElementDiff diffElement) {
		Node n = graph.createAndAddNode(diffElement.getUid().toString());
		nodesByUUID.put(diffElement.getUid().toString(), n);
		return n;
	}

	private void clearRules() {
		graph.clear();
	}

	public JPanel getGraphPanel() {
		return graph.getViewPanel();
	}
}
