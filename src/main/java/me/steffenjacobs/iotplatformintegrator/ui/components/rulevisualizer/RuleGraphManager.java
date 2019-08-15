package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;
import me.steffenjacobs.iotplatformintegrator.ui.util.Pair;

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<String, Node> nodesByUUID = new HashMap<>();
	private final Map<String, SharedRule> ruleByUUID = new HashMap<>();
	private final CopyOnWriteArraySet<Pair<String>> edges = new CopyOnWriteArraySet<>();

	private final AtomicBoolean nextSelectedRuleIsTarget = new AtomicBoolean(false);
	private final AtomicReference<String> nextSelectedRuleIsTargetId = new AtomicReference<>("");

	private final ClickableGraph graph;

	public RuleGraphManager() {
		graph = createVisualization();

		EventBus.getInstance().addEventHandler(EventType.REMOTE_RULE_ADDED, e -> visualizeRule(((WithSharedRuleEvent) e).getSelectedRule()));
		EventBus.getInstance().addEventHandler(EventType.CLEAR_ALL_REMOTE_ITEMS, e -> clear());
		EventBus.getInstance().addEventHandler(EventType.RULE_DIFF_CHANGE, e -> visualizeRuleDiff(((RuleDiffChangeEvent) e).getDiffElement()));
		EventBus.getInstance().addEventHandler(EventType.RULE_DIFF_ADDED, e -> visualizeRuleDiff(((RuleDiffAddedEvent) e).getRuleDiffParts()));
		EventBus.getInstance().addEventHandler(EventType.REMOTE_ITEM_ADDED, e -> graph.refreshEdges(edges));
		EventBus.getInstance().addEventHandler(EventType.SELECT_TARGET_RULE, e -> {
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
					SharedRule clickedRule = App.getRemoteRuleCache().getRuleByName(id);
					RuleDiffParts diff = App.getRuleDiffCache().getRuleDiffParts(graph.getLastSelectedNode().getId());
					if (diff == null) {
						JOptionPane.showMessageDialog(null, "You can only set a target when you are in a transformation state.", "Rule cannot be matched to another rule.",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					SharedRule rebuiltRule = App.getRuleChangeEventStore().rebuildRule(diff);

					List<String> warnings = App.getRuleChangeEventStore().checkRulesCompatible(clickedRule, rebuiltRule);
					if (warnings.isEmpty()) {
						EventBus.getInstance().fireEvent(new StoreRuleToDatabaseEvent(null, id, false));
						edges.add(Pair.of(nextSelectedRuleIsTargetId.get(), id));
						graph.refreshEdges(edges);
						graph.selectNode(id, false, nodesByUUID, ruleByUUID);
					} else {
						JOptionPane.showMessageDialog(null, "Please select a rule that is compatible with your current transformation state:\n" + String.join("\n", warnings),
								"Rule cannot be matched", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					graph.selectNode(id, false, nodesByUUID, ruleByUUID);
				}
			}

			@Override
			public void buttonPushed(String id) {
				// ignore
			}
		});
	}

	private void visualizeRule(SharedRule rule) {
		Node n = graph.createAndAddNode(rule.getName(), false);
		if (n != null) {
			nodesByUUID.put(rule.getName(), n);
			ruleByUUID.put(rule.getName(), rule);
			graph.refreshEdges(edges);
		}
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
		visualizeRuleDiff(diffElement, prevDiffUid, diffElement.getTargetRule().orElse(null), sourceRuleName, true);
	}

	private void visualizeRuleDiff(SharedRuleElementDiff diffElement, String prevDiffUid, String targetRuleName, String sourceRuleName, boolean silentAutoselect) {
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
				createDiffNode(diffElement, silentAutoselect);
				if (ClickableGraph.enableFineLogging) {
					LOG.warn("Could not find diff anchor node {}.", prevDiffUid);
				}
				return;
			}
		} else {

			if (sourceRuleName != null) {
				createDiffNode(diffElement, silentAutoselect);
				if (ClickableGraph.enableFineLogging) {
					LOG.warn("Could not find source rule for diff {}.", sourceRuleName);
				}
				return;
			}
			anchor = nodesByUUID.get(sourceRuleName);
			if (anchor == null) {
				createDiffNode(diffElement, silentAutoselect);
				if (ClickableGraph.enableFineLogging) {
					LOG.warn("Could not find source rule node {}.", sourceRuleName);
				}
				return;
			}
		}

		createDiffNode(diffElement, silentAutoselect);
	}

	private void visualizeRuleDiff(RuleDiffParts ruleDiffParts) {
		visualizeRuleDiff(ruleDiffParts.getRuleDiff(), ruleDiffParts.getPrevDiffId(), ruleDiffParts.getTargetRuleName(), ruleDiffParts.getSourceRuleName(), false);
	}

	private void createDiffNode(SharedRuleElementDiff diffElement, boolean silentAutoselect) {
		Node n = graph.createAndAddNode(diffElement.getUid().toString(), true);
		if (n != null) {
			nodesByUUID.put(diffElement.getUid().toString(), n);
			graph.refreshEdges(edges);
			if (silentAutoselect) {
				graph.selectNode(n.getId(), true, nodesByUUID, ruleByUUID);
			}
		}
	}

	private void clear() {
		graph.clear();
		edges.clear();
		nodesByUUID.clear();
		ruleByUUID.clear();
	}

	public JPanel getGraphPanel() {
		return graph.getViewPanel();
	}

}
