package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

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
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;
import me.steffenjacobs.iotplatformintegrator.ui.util.Pair;

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<String, Node> nodesByUUID = new HashMap<>();
	private final CopyOnWriteArraySet<Pair<String>> edges = new CopyOnWriteArraySet<>();

	private final ClickableGraph graph;

	public RuleGraphManager() {
		graph = createVisualization();

		EventBus.getInstance().addEventHandler(EventType.RemoteRuleAdded, e -> visualizeRule(((WithSharedRuleEvent) e).getSelectedRule()));
		EventBus.getInstance().addEventHandler(EventType.ClearAllRemoteRules, e -> clearRules());
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> visualizeRuleDiff(((RuleDiffChangeEvent) e).getDiffElement()));
		EventBus.getInstance().addEventHandler(EventType.RuleDiffAdded, e -> visualizeRuleDiff(((RuleDiffAddedEvent) e).getRuleDiffParts()));
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
		Node n = graph.createAndAddNode(rule.getName());
		nodesByUUID.put(rule.getName(), n);
		graph.refreshEdges(edges);
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement) {
		String sourceRuleName;
		if (diffElement.getSourceRule().isPresent()) {
			sourceRuleName = diffElement.getSourceRule().get().getName();
		} else {
			sourceRuleName = null;
		}

		String prevDiffUid;
		if (diffElement.getPrevDiff().isPresent()) {
			prevDiffUid = diffElement.getPrevDiff().get().getUid().toString();
		} else {
			prevDiffUid = null;
		}
		visualizeRuleDiff(diffElement, prevDiffUid, diffElement.getTargetRule().orElse(null), sourceRuleName);
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement, String prevDiffUid, String targetRuleName, String sourceRuleName) {
		final Node anchor;

		if (prevDiffUid != null) {
			edges.add(Pair.of(prevDiffUid, diffElement.getUid().toString()));
		} else {
			edges.add(Pair.of(sourceRuleName, diffElement.getUid().toString()));
		}

		if (targetRuleName != null) {
			edges.add(Pair.of(diffElement.getUid().toString(), targetRuleName));
		}

		if (prevDiffUid != null) {
			anchor = nodesByUUID.get(prevDiffUid);
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find diff anchor node {}.", prevDiffUid);
				return;
			}
		} else {

			if (sourceRuleName != null) {
				createNode(diffElement);
				LOG.warn("Could not find source rule for diff {}.", sourceRuleName);
				return;
			}
			anchor = nodesByUUID.get(sourceRuleName);
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find source rule node {}.", sourceRuleName);
				return;
			}
		}

		createNode(diffElement);
	}

	private void visualizeRuleDiff(RuleDiffParts ruleDiffParts) {
		visualizeRuleDiff(ruleDiffParts.getRuleDiff(), ruleDiffParts.getPrevDiffId(), ruleDiffParts.getTargetRuleName(), ruleDiffParts.getSourceRuleName());
	}

	private Node createNode(SharedRuleElementDiff diffElement) {
		Node n = graph.createAndAddNode(diffElement.getUid().toString());
		nodesByUUID.put(diffElement.getUid().toString(), n);
		graph.refreshEdges(edges);
		return n;
	}

	private void clearRules() {
		graph.clear();
		edges.clear();
	}

	public JPanel getGraphPanel() {
		return graph.getViewPanel();
	}
}
