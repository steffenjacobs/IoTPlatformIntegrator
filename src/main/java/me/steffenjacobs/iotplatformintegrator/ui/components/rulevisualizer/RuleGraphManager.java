package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.graphstream.graph.Node;
import org.graphstream.ui.view.ViewerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RefreshRuleDiffsEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectTargetRuleEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleDiffChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;
import me.steffenjacobs.iotplatformintegrator.ui.util.Pair;

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<Node, Boolean> nodeWithType = new HashMap<>();
	private final Map<String, Node> nodesByUUID = new HashMap<>();
	private final Map<String, SharedRule> ruleByUUID = new HashMap<>();
	private final CopyOnWriteArraySet<Pair<String>> edges = new CopyOnWriteArraySet<>();

	private Node lastSelectedNode = null;

	private final AtomicBoolean nextSelectedRuleIsTarget = new AtomicBoolean(false);
	private final AtomicReference<String> nextSelectedRuleIsTargetId = new AtomicReference<>("");

	private final ClickableGraph graph;

	public RuleGraphManager() {
		graph = createVisualization();

		EventBus.getInstance().addEventHandler(EventType.RemoteRuleAdded, e -> visualizeRule(((WithSharedRuleEvent) e).getSelectedRule()));
		EventBus.getInstance().addEventHandler(EventType.ClearAllRemoteRules, e -> clearRules());
		EventBus.getInstance().addEventHandler(EventType.RuleDiffChangeEvent, e -> visualizeRuleDiff(((RuleDiffChangeEvent) e).getDiffElement()));
		EventBus.getInstance().addEventHandler(EventType.RuleDiffAdded, e -> visualizeRuleDiff(((RuleDiffAddedEvent) e).getRuleDiffParts()));
		EventBus.getInstance().addEventHandler(EventType.RemoteItemAdded, e -> graph.refreshEdges(edges));
		EventBus.getInstance().addEventHandler(EventType.SelectTargetRule, e -> {
			nextSelectedRuleIsTarget.set(true);
		});

		JPopupMenu popup = new JPopupMenu();
		JMenuItem refreshButton = new JMenuItem("Refresh");
		refreshButton.addActionListener(e -> {
			App.getRemoteRuleController().refreshRules();
			EventBus.getInstance().fireEvent(new RefreshRuleDiffsEvent());
		});

		popup.add(refreshButton);

		graph.getViewPanel().setComponentPopupMenu(popup);
	}

	private ClickableGraph createVisualization() {
		return new ClickableGraph(new ViewerListener() {

			@Override
			public void viewClosed(String viewName) {
				// should not happen
			}

			@Override
			public void buttonReleased(String id) {
				if (nextSelectedRuleIsTarget.getAndSet(false)) {
					EventBus.getInstance().fireEvent(new StoreRuleToDatabaseEvent(null, id, false));
					edges.add(Pair.of(nextSelectedRuleIsTargetId.getAndSet(""), id));
					graph.refreshEdges(edges);
				}
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
						EventBus.getInstance().fireEvent(new SelectedRuleDiffChangeEvent(App.getRuleDiffCache().getRuleDiffParts(id)));
						lastSelectedNode = node;

					} else if (nodeType == false) {
						EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(ruleByUUID.get(id)));
						node.addAttribute("ui.style", "size: 20px;");
						node.addAttribute("ui.style", "stroke-mode: plain;");
						node.addAttribute("ui.style", "stroke-color: #0b283d;");
						node.addAttribute("ui.style", "stroke-width: 3px;");
						lastSelectedNode = node;
					}
				}

				// TODO click on diff
			}

			@Override
			public void buttonPushed(String id) {
				// ignore
			}
		});
	}

	private void visualizeRule(SharedRule rule) {
		Node n = graph.createAndAddNode(rule.getName(), false);
		nodesByUUID.put(rule.getName(), n);
		nodeWithType.put(n, false);
		ruleByUUID.put(rule.getName(), rule);
		graph.refreshEdges(edges);
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement) {
		nextSelectedRuleIsTargetId.set(diffElement.getUid().toString());
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
				createDiffNode(diffElement);
				LOG.warn("Could not find diff anchor node {}.", prevDiffUid);
				return;
			}
		} else {

			if (sourceRuleName != null) {
				createDiffNode(diffElement);
				LOG.warn("Could not find source rule for diff {}.", sourceRuleName);
				return;
			}
			anchor = nodesByUUID.get(sourceRuleName);
			if (anchor == null) {
				createDiffNode(diffElement);
				LOG.warn("Could not find source rule node {}.", sourceRuleName);
				return;
			}
		}

		createDiffNode(diffElement);
	}

	private void visualizeRuleDiff(RuleDiffParts ruleDiffParts) {
		visualizeRuleDiff(ruleDiffParts.getRuleDiff(), ruleDiffParts.getPrevDiffId(), ruleDiffParts.getTargetRuleName(), ruleDiffParts.getSourceRuleName());
	}

	private Node createDiffNode(SharedRuleElementDiff diffElement) {
		Node n = graph.createAndAddNode(diffElement.getUid().toString(), true);
		nodesByUUID.put(diffElement.getUid().toString(), n);
		nodeWithType.put(n, true);
		graph.refreshEdges(edges);
		return n;
	}

	private void clearRules() {
		graph.clear();
		edges.clear();
		nodesByUUID.clear();
		ruleByUUID.clear();
		nodeWithType.clear();
	}

	public JPanel getGraphPanel() {
		return graph.getViewPanel();
	}
}
