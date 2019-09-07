package me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.graphstream.graph.Node;
import org.graphstream.ui.view.ViewerListener;
import org.jfree.ui.tabbedui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.DiffType;
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
import me.steffenjacobs.iotplatformintegrator.ui.components.rulevisualizer.ClickableGraph.SelectionType;
import me.steffenjacobs.iotplatformintegrator.ui.util.Pair;
import me.steffenjacobs.iotplatformintegrator.ui.util.PlaceholderTextField;

/** @author Steffen Jacobs */
public class RuleGraphManager {

	private static final Logger LOG = LoggerFactory.getLogger(RuleGraphManager.class);

	private final Map<String, Node> nodesByUUID = new HashMap<>();
	private final Map<String, SharedRule> ruleByUUID = new HashMap<>();
	private final Map<UUID, SharedRuleElementDiff> diffByUUID = new HashMap<>();
	private final CopyOnWriteArraySet<Pair<String>> edges = new CopyOnWriteArraySet<>();

	private final AtomicBoolean nextSelectedRuleIsTarget = new AtomicBoolean(false);
	private final AtomicReference<String> nextSelectedRuleIsTargetId = new AtomicReference<>("");

	private final ClickableGraph graph;

	private JPanel graphPanel;

	private SharedRule lastSelectedRule;

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
		EventBus.getInstance().addEventHandler(EventType.RULE_CHANGE, e -> checkIfCurrentTransformationStateExistsAsRule(((WithSharedRuleEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RULE_RENDER, e -> this.lastSelectedRule = ((WithSharedRuleEvent) e).getSelectedRule());

		final JPanel buttonPanel = new JPanel(new FlowLayout());

		// find current rule button
		final JButton findCurrentRuleButton = new JButton("Find current rule");
		findCurrentRuleButton.addActionListener(e -> {
			if (lastSelectedRule != null) {

				final Collection<SharedRule> rules = new ArrayList<>();
				for (SharedRule rule : App.getRemoteRuleCache().getRules()) {
					List<String> warnings = App.getRuleChangeEventStore().checkRulesCompatible(lastSelectedRule, rule, false);
					if (warnings.isEmpty()) {
						rules.add(rule);
					}
				}
				selectRuleNodes(rules);
			}
		});

		// search field
		final PlaceholderTextField searchItemName = new PlaceholderTextField("Search for Item Name...");
		searchItemName.setColumns(15);
		final Font f = searchItemName.getFont();
		searchItemName.setFont(new Font(f.getName(), f.getStyle(), 18));
		searchItemName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				visualizeItemFilter(searchItemName.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				visualizeItemFilter(searchItemName.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				visualizeItemFilter(searchItemName.getText());
			}
		});

		// refresh button
		final ActionListener refreshAction = e -> {
			App.getRemoteRuleController().refreshRules();
			EventBus.getInstance().fireEvent(new RefreshRuleDiffsEvent());
		};

		final JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(refreshAction);

		// rule diff filters

		final JButton filterButton = new JButton("Filter...");
		filterButton.addActionListener(new FilterPopupActionHandler(filterButton));

		// add buttons to panel
		buttonPanel.add(refreshButton);
		buttonPanel.add(searchItemName);
		buttonPanel.add(findCurrentRuleButton);
		buttonPanel.add(filterButton);

		// add button panel
		graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(buttonPanel, BorderLayout.NORTH);

		graphPanel.add(graph.getViewPanel(), BorderLayout.CENTER);

		// setup popup menu
		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem refreshMenu = new JMenuItem("Refresh");
		refreshMenu.addActionListener(refreshAction);

		popupMenu.add(refreshMenu);

		graph.getViewPanel().setComponentPopupMenu(popupMenu);

	}

	private void visualizeDiffFilter(Collection<SelectionType> filters) {
		// clear selection
		diffByUUID.values().forEach(r -> graph.selectFilterNode(r.getUid().toString(), false, nodesByUUID::get, SelectionType.DESELECT));

		// apply new selection
		filters.forEach(filter -> App.getRuleDiffCache().getRuleDiffsWithAnyFilter(getAssociatedDiffTypes(filter)).stream().map(RuleDiffParts::getRuleDiff)
				.collect(Collectors.toList()).forEach(r -> graph.selectFilterNode(r.getUid().toString(), true, nodesByUUID::get, filter)));
	}

	private Set<DiffType> getAssociatedDiffTypes(SelectionType selectionType) {
		switch (selectionType) {
		case DIFF_FILTER_COSMETIC:
			return set(DiffType.DESCRIPTION_CHANGED, DiffType.LABEL_CHANGED);
		case DIFF_FILTER_DIFF_FULL_CREATED:
			return set(DiffType.FULL);
		case DIFF_FILTER_DIFF_FULL_DELETED:
			return set(DiffType.FULL_DELETED);
		case DIFF_FILTER_CREATE:
			return set(DiffType.ACTION_TYPE_VALUE_ADDED, DiffType.CONDITION_TYPE_VALUE_ADDED, DiffType.TRIGGER_TYPE_VALUE_ADDED);
		case DIFF_FILTER_DELETE:
			return set(DiffType.ACTION_TYPE_VALUE_DELETED, DiffType.CONDITION_TYPE_VALUE_DELETED, DiffType.TRIGGER_TYPE_VALUE_DELETED);
		case DIFF_FILTER_UPDATE:
			return set(DiffType.ACTION_TYPE_CHANGED, DiffType.ACTION_TYPE_VALUE_UPDATED, DiffType.CONDITION_TYPE_CHANGED, DiffType.CONDITION_TYPE_VALUE_UPDATED,
					DiffType.TRIGGER_TYPE_CHANGE, DiffType.TRIGGER_TYPE_VALUE_UPDATED);
		case RULE_FILTER:
		case DESELECT:
		case UNKNOWN:
			return Collections.emptySet();
		}
		return Collections.emptySet();
	}

	@SafeVarargs
	private final <T> Set<T> set(T... t) {
		Set<T> set = new HashSet<>();
		for (T tt : t) {
			set.add(tt);
		}
		return set;
	}

	private void selectRuleNodes(Iterable<SharedRule> rules) {
		// clear selection
		ruleByUUID.values().forEach(r -> graph.selectFilterNode(r.getName(), false, nodesByUUID::get, SelectionType.RULE_FILTER));

		// apply new selection
		rules.forEach(r -> graph.selectFilterNode(r.getName(), true, nodesByUUID::get, SelectionType.RULE_FILTER));
	}

	private void visualizeItemFilter(String searchText) {
		selectRuleNodes(App.getRemoteRuleCache().getRulesWithItemNameContaining(searchText));
	}

	private void checkIfCurrentTransformationStateExistsAsRule(SharedRule updatedRule) {
		for (SharedRule rule : App.getRemoteRuleCache().getRules()) {
			if (rule != updatedRule && App.getRuleChangeEventStore().checkRulesCompatible(rule, updatedRule).isEmpty()) {
				EventBus.getInstance().fireEvent(new StoreRuleToDatabaseEvent(null, rule.getName(), false));
				edges.add(Pair.of(nextSelectedRuleIsTargetId.get(), rule.getName()));
				graph.refreshEdges(edges);
				graph.selectNode(rule.getName(), true, nodesByUUID, ruleByUUID);
				nextSelectedRuleIsTargetId.set(rule.getName());
				return;
			}
		}
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
						nextSelectedRuleIsTargetId.set(id);
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
		diffByUUID.put(diffElement.getUid(), diffElement);

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
		diffByUUID.clear();
	}

	public JPanel getGraphPanel() {
		return graphPanel;
	}

	private class FilterPopupActionHandler implements ActionListener {

		private final JPanel checkBoxPanel = new JPanel(new VerticalLayout());
		private final JButton filterButton;
		
		private final Set<SelectionType> selectedFilters = new HashSet<>();
		private final FocusListener fl;

		
		private Popup popup = null;
		private boolean showing = false;

		public FilterPopupActionHandler(JButton filterButton) {
			this.filterButton = filterButton;
			fl = new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					e.getComponent().removeFocusListener(this);
					if (e.getOppositeComponent() == checkBoxPanel) {
						e.getOppositeComponent().addFocusListener(this);
						return;
					}
					for (Component child : checkBoxPanel.getComponents()) {
						if (e.getOppositeComponent() == child) {
							e.getOppositeComponent().addFocusListener(this);
							return;
						}
					}
					hidePopup();
				}
			};

			SelectionType.displayableValues().forEach(selectionType -> {
				final JCheckBox checkBox = new JCheckBox(selectionType.getDisplayString());
				checkBox.addActionListener(c -> {
					if (checkBox.isSelected()) {
						selectedFilters.add(selectionType);
					} else {
						selectedFilters.remove(selectionType);
					}
					visualizeDiffFilter(selectedFilters);
				});
				checkBox.addFocusListener(fl);
				checkBoxPanel.add(checkBox);
			});
			checkBoxPanel.addFocusListener(fl);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			removeFocusListenerIfPresent(filterButton, fl);
			filterButton.addFocusListener(fl);
			if (!showing) {
				final Point filterButtonLocation = filterButton.getLocationOnScreen();
				popup = PopupFactory.getSharedInstance().getPopup(filterButton, checkBoxPanel, filterButtonLocation.x, filterButtonLocation.y + filterButton.getHeight());
				showing = true;
				popup.show();
			} else {
				hidePopup();
			}
		}

		private void removeFocusListenerIfPresent(Component c, FocusListener f) {
			for (FocusListener ff : c.getFocusListeners()) {
				if (ff == f) {
					c.removeFocusListener(f);
				}
			}
		}

		private void hidePopup() {
			showing = false;
			if (popup != null) {
				popup.hide();
			}
		}
	}

}
