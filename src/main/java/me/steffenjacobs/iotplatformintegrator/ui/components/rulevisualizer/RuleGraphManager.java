package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.Pair;
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

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<String, Node> nodesByUUID = new HashMap<>();
	private final Set<Pair<String, String>> edges = new HashSet<>();

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
		Node n = graph.createAndAddNode(rule.getId());
		nodesByUUID.put(rule.getName(), n);
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement) {
		visualizeRuleDiff(diffElement, diffElement.getPrevDiff().get().getUid().toString(), diffElement.getTargetRule().orElse(null));
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement, String prevDiffUid, String targetRuleId) {
		final Node anchor;
		edges.add(Pair.of(prevDiffUid, diffElement.getUid().toString()));
		
		if(targetRuleId != null) {
			edges.add(Pair.of(diffElement.getUid().toString(), targetRuleId));
		}
		
		if (prevDiffUid != null) {
			anchor = nodesByUUID.get(prevDiffUid);
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find diff anchor node {}.", prevDiffUid);
				return;
			}
		} else {

			if (!diffElement.getSourceRule().isPresent()) {
				createNode(diffElement);
				LOG.warn("Could not find source rule for diff {}.", diffElement.getUid().toString());
				return;
			}
			anchor = nodesByUUID.get(diffElement.getSourceRule().get().getId());
			if (anchor == null) {
				createNode(diffElement);
				LOG.warn("Could not find source rule node {}.", diffElement.getSourceRule().get().getId());
				return;
			}
		}

		createNode(diffElement);
	}

	private void visualizeRuleDiff(RuleDiffParts ruleDiffParts) {
		visualizeRuleDiff(ruleDiffParts.getRuleDiff(), ruleDiffParts.getPrevDiffId(), ruleDiffParts.getTargetRuleName());
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
