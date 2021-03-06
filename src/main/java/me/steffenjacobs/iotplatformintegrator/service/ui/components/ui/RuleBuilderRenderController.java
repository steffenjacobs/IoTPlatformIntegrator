package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.RuleRelatedAnnotation;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.ConditionType.ConditionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.TriggerType.TriggerTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleRenderEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ActionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ConditionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.TriggerRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.VisualRenderingStrategy;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectoryHolder;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.RuleAnalyzer;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ActionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.TriggerElement;

/** @author Steffen Jacobs */
public class RuleBuilderRenderController implements RuleComponentRegistry, RuleAnalyzer {

	private static final Logger LOG = LoggerFactory.getLogger(RuleBuilderRenderController.class);

	private final TriggerRenderer<Component> triggerRenderer = new TriggerRenderer<>(new VisualRenderingStrategy(this));
	private final ConditionRenderer<Component> conditionRenderer = new ConditionRenderer<>(new VisualRenderingStrategy(this));
	private final ActionRenderer<Component> actionRenderer = new ActionRenderer<>(new VisualRenderingStrategy(this));

	private final RuleBuilder ruleBuilder;

	private final Map<UUID, DynamicElement> renderedRuleElements = new HashMap<>();
	private final Map<UUID, SharedRuleElement> ruleElements = new HashMap<>();
	private final Map<SharedItem, Set<JComboBox<SharedItem>>> itemElements = new HashMap<>();

	private final Map<Component, RuleRelatedAnnotation> annotatedComponents = new HashMap<>();

	private SharedRule rule = null;

	private final RuleElementRecommender recommender;
	private final RuleElementValidator validator = new RuleElementValidator(this);

	public Optional<SharedRule> getDisplayedRule() {
		return rule == null ? Optional.empty() : Optional.of(rule);
	}

	public RuleBuilderRenderController(RuleBuilder ruleBuilder, SettingService settingService) {
		recommender = new RuleElementRecommender(this, settingService);
		new RuleMutator(this, settingService);
		this.ruleBuilder = ruleBuilder;
		ruleBuilder.setRenderController(this);

		EventBus.getInstance().addEventHandler(EventType.SELECTED_RULE_CHANGE, e -> renderRule(((WithSharedRuleEvent) e).getSelectedRule()));
		EventBus.getInstance().addEventHandler(EventType.REMOTE_RULE_CHANGE, e -> renderRule(((WithSharedRuleEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RULE_CHANGE, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				renderRule(rule);
			}
			ruleBuilder.onSelectedRuleChanged(event.getSelectedRule());
		});
		EventBus.getInstance().addEventHandler(EventType.RULE_ELEMENT_CHANGE, e -> {
			RuleElementChangeEvent event = (RuleElementChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				SharedRuleElement elem = ruleElements.get(event.getSourceId());
				if (elem instanceof SharedTrigger) {
					rule.getTriggers().remove(elem);
					SharedTrigger trigger = parseTriggerFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), trigger);
					rule.getTriggers().add(trigger);
					EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, trigger, elem));

				} else if (elem instanceof SharedCondition) {
					rule.getConditions().remove(elem);
					SharedCondition condition = parseConditionFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), condition);
					rule.getConditions().add(condition);
					EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, condition, elem));

				} else if (elem instanceof SharedAction) {
					rule.getActions().remove(elem);
					SharedAction action = parseActionFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), action);
					rule.getActions().add(action);
					EventBus.getInstance().fireEvent(new RuleChangeEvent(rule, action, elem));
				} else {
					LOG.error("Invalid element type: " + elem);
				}
			}
		});
	}

	private SharedTrigger parseTriggerFromView(UUID source) {
		SharedTrigger oldTrigger = (SharedTrigger) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedTrigger trigger = new SharedTrigger(oldTrigger.getTriggerTypeContainer().getTriggerType(), properties, oldTrigger.getDescription(), oldTrigger.getLabel(),
				oldTrigger.getRelativeElementId());
		return trigger;
	}

	private SharedCondition parseConditionFromView(UUID source) {
		SharedCondition oldCondition = (SharedCondition) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedCondition condition = new SharedCondition(oldCondition.getConditionTypeContainer().getConditionType(), properties, oldCondition.getDescription(),
				oldCondition.getLabel(), oldCondition.getRelativeElementId());
		return condition;
	}

	private SharedAction parseActionFromView(UUID source) {
		SharedAction oldAction = (SharedAction) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedAction action = new SharedAction(oldAction.getActionTypeContainer().getActionType(), properties, oldAction.getDescription(), oldAction.getLabel(),
				oldAction.getRelativeElementId());
		return action;
	}

	private Map<String, Object> parsePropertiesFromView(DynamicElement elem) {
		final Map<String, Object> properties = new HashMap<>();

		for (Component strategyElement : elem.getStrategyElements()) {
			RuleRelatedAnnotation annotation = annotatedComponents.get(strategyElement);

			if (annotation == null || annotation.getRuleElementSpecificKey() == null) {
				continue;
			}
			switch (annotation.getRuleElementSpecificKey().getKeyString()) {
			case "operator":
			case "command":
			case "itemName":
				if (strategyElement instanceof JComboBox<?>) {
					JComboBox<?> combo = (JComboBox<?>) strategyElement;
					properties.put(annotation.getRuleElementSpecificKey().getKeyString(), combo.getSelectedItem());
				} else {
					JTextField text = (JTextField) strategyElement;
					properties.put(annotation.getRuleElementSpecificKey().getKeyString(), text.getText());
				}
				break;
			case "enable":
			case "considerConditions":
				JTextField textB = (JTextField) strategyElement;
				properties.put(annotation.getRuleElementSpecificKey().getKeyString(), Boolean.parseBoolean(textB.getText()));
				break;
			case "ruleUIDs":
			case "value":
			case "type":
			case "script":
			case "sink":
			case "sound":
			case "state":
			case "startTime":
			case "endTime":
			case "time":
			case "previous_state":
			case "channel":
			case "event":
			case "event_data":
				if (strategyElement instanceof JTextField) {
					JTextField text = (JTextField) strategyElement;
					properties.put(annotation.getRuleElementSpecificKey().getKeyString(), text.getText());
				} else if (strategyElement instanceof JComboBox<?>) {
					JComboBox<?> box = (JComboBox<?>) strategyElement;
					properties.put(annotation.getRuleElementSpecificKey().getKeyString(), ((Command) box.getSelectedItem()).name());
				} else {
					LOG.error("Not implemented component type found during parsing the properties from the view: {}", strategyElement.getClass().getName());
				}
				break;
			case "text":
				// ignore text changes since they should not occur
				break;
			default:
				LOG.error("Invalid rule element specific key for parsing from view: {}", annotation.getRuleElementSpecificKey());
			}
		}
		return properties;
	}

	@Override
	public void addAnnotatedComponent(Component component, SharedTypeSpecificKey ruleElementSpecificKey, int index) {
		annotatedComponents.put(component, new RuleRelatedAnnotation(index, ruleElementSpecificKey));
	}

	public RuleRelatedAnnotation getAnnotationFromComponent(Component component) {
		return annotatedComponents.get(component);
	}

	@Override
	public void clearComponents() {
		annotatedComponents.clear();
	}

	@Override
	public SharedRuleElement getRuleElementById(UUID uuid) {
		return ruleElements.get(uuid);
	}

	@Override
	public SharedRuleElement removeRuleElementById(UUID uuid) {
		return ruleElements.remove(uuid);
	}

	public void renderRule(SharedRule rule) {
		this.rule = rule;
		renderedRuleElements.clear();
		ruleBuilder.clear();
		ruleElements.clear();
		annotatedComponents.clear();
		itemElements.clear();
		if (rule == null) {
			return;
		}
		ruleBuilder.setHeader(rule.getName(), rule.getStatus(), rule.getDescription());

		final List<SharedTrigger> triggers = new ArrayList<>();
		triggers.addAll(rule.getTriggers());
		triggers.sort((t1, t2) -> t1.getRelativeElementId() - t2.getRelativeElementId());
		for (SharedTrigger trigger : triggers) {
			ruleBuilder.appendDynamicElement(renderTrigger(trigger, triggerRenderer));
		}

		final List<SharedCondition> conditions = new ArrayList<>();
		conditions.addAll(rule.getConditions());
		conditions.sort((t1, t2) -> t1.getRelativeElementId() - t2.getRelativeElementId());
		for (SharedCondition condition : conditions) {
			ruleBuilder.appendDynamicElement(renderCondition(condition, conditionRenderer));
		}

		final List<SharedAction> actions = new ArrayList<>();
		actions.addAll(rule.getActions());
		actions.sort((t1, t2) -> t1.getRelativeElementId() - t2.getRelativeElementId());
		for (SharedAction action : actions) {
			ruleBuilder.appendDynamicElement(renderAction(action, actionRenderer));
		}
		EventBus.getInstance().fireEvent(new RuleRenderEvent(rule));
	}

	private DynamicElement renderTrigger(SharedTrigger trigger, TriggerRenderer<Component> triggerRenderer) {
		UUID uuid = UUID.randomUUID();
		TriggerElement elem = new TriggerElement(uuid);
		elem.setTriggerTypeContainer(trigger.getTriggerTypeContainer());

		String label = trigger.getLabel();
		String description = trigger.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		Collection<Component> strategyElements = triggerRenderer.renderTrigger(trigger);
		addComponentListeners(uuid, strategyElements, ElementType.CONDITION);
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);
		elem.setStrategyElements(strategyElements);
		ruleElements.put(uuid, trigger);
		renderedRuleElements.put(uuid, elem);

		if (trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().containsKey(TriggerTypeSpecificKey.ItemName)) {
			for (Component c : strategyElements) {
				if (c instanceof JComboBox) {
					try {
						@SuppressWarnings("unchecked")
						final JComboBox<SharedItem> box = (JComboBox<SharedItem>) c;
						if (box.getSelectedItem() instanceof SharedItem) {
							final SharedItem item = (SharedItem) trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().get(TriggerTypeSpecificKey.ItemName);
							itemElements.putIfAbsent(item, new HashSet<>());
							itemElements.get(item).add(box);
						}
					} catch (ClassCastException e) {
						// nothing to do
					}
				}
			}
		}
		return elem;
	}

	private DynamicElement renderAction(SharedAction action, ActionRenderer<Component> actionRenderer) {
		UUID uuid = UUID.randomUUID();
		ActionElement elem = new ActionElement(uuid);
		elem.setActionTypeContainer(action.getActionTypeContainer());

		String label = action.getLabel();
		String description = action.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		Collection<Component> strategyElements = actionRenderer.renderAction(action);
		addComponentListeners(uuid, strategyElements, ElementType.ACTION);
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);
		elem.setStrategyElements(strategyElements);
		renderedRuleElements.put(uuid, elem);
		ruleElements.put(uuid, action);

		if (action.getActionTypeContainer().getActionTypeSpecificValues().containsKey(ActionTypeSpecificKey.ItemName)) {
			for (Component c : strategyElements) {
				if (c instanceof JComboBox) {
					try {
						@SuppressWarnings("unchecked")
						final JComboBox<SharedItem> box = (JComboBox<SharedItem>) c;
						if (box.getSelectedItem() instanceof SharedItem) {
							final SharedItem item = (SharedItem) action.getActionTypeContainer().getActionTypeSpecificValues().get(ActionTypeSpecificKey.ItemName);
							itemElements.putIfAbsent(item, new HashSet<>());
							itemElements.get(item).add(box);
						}
					} catch (ClassCastException e) {
						// nothing to do
					}
				}
			}
		}
		return elem;
	}

	private ConditionElement renderCondition(SharedCondition condition, ConditionRenderer<Component> conditionRenderer) {
		UUID uuid = UUID.randomUUID();
		ConditionElement elem = new ConditionElement(uuid);
		elem.setConditionTypeContainer(condition.getConditionTypeContainer());

		String label = condition.getLabel();
		String description = condition.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));
		Collection<Component> strategyElements = conditionRenderer.renderCondition(condition);

		addComponentListeners(uuid, strategyElements, ElementType.CONDITION);
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);

		elem.setStrategyElements(strategyElements);
		ruleElements.put(uuid, condition);
		renderedRuleElements.put(uuid, elem);

		if (condition.getConditionTypeContainer().getConditionTypeSpecificValues().containsKey(ConditionTypeSpecificKey.ItemName)) {
			for (Component c : strategyElements) {
				if (c instanceof JComboBox) {
					try {
						@SuppressWarnings("unchecked")
						final JComboBox<SharedItem> box = (JComboBox<SharedItem>) c;
						if (box.getSelectedItem() instanceof SharedItem) {
							final SharedItem item = (SharedItem) condition.getConditionTypeContainer().getConditionTypeSpecificValues().get(ConditionTypeSpecificKey.ItemName);
							itemElements.putIfAbsent(item, new HashSet<>());
							itemElements.get(item).add(box);
						}
					} catch (ClassCastException e) {
						// nothing to do
					}
				}
			}
		}
		return elem;
	}

	private void addComponentListeners(UUID uuid, Collection<Component> strategyElements, ElementType elementType) {
		int counter = 0;
		for (Component strategyElement : strategyElements) {
			RuleRelatedAnnotation annotation = getAnnotationFromComponent(strategyElement);

			// update annotation
			addAnnotatedComponent(strategyElement, annotation.getRuleElementSpecificKey(), counter);

			if (annotation.getRuleElementSpecificKey() == null) {
				continue;
			}
			// add listener
			switch (annotation.getRuleElementSpecificKey().getKeyString()) {
			case "operator":
			case "command":
			case "itemName":
				if (strategyElement instanceof JComboBox<?>) {
					JComboBox<?> combo = (JComboBox<?>) strategyElement;
					combo.addActionListener(l -> EventBus.getInstance().fireEvent(new RuleElementChangeEvent(elementType, uuid, rule)));
				} else {
					JTextField text = (JTextField) strategyElement;
					text.addKeyListener(new KeyAdapter() {
						public void keyReleased(java.awt.event.KeyEvent e) {
							if (e.getKeyCode() == KeyEvent.VK_ENTER)
								EventBus.getInstance().fireEvent(new RuleElementChangeEvent(elementType, uuid, rule));
						};
					});
				}
				break;
			case "enable":
			case "considerConditions":
				// TODO: validation for boolean
			case "startTime":
			case "endTime":
			case "time":
				// TODO: validation for time
			case "channel":
			case "event":
			case "event_data":
			case "value":
			case "ruleUIDs":
			case "type":
			case "script":
			case "sink":
			case "sound":
			case "previous_state":
			case "state":
				// TODO: validation, if this is an item or a command
				if (strategyElement instanceof JTextField) {
					JTextField text = (JTextField) strategyElement;
					text.addKeyListener(new KeyAdapter() {
						public void keyReleased(java.awt.event.KeyEvent e) {
							if (e.getKeyCode() == KeyEvent.VK_ENTER)
								EventBus.getInstance().fireEvent(new RuleElementChangeEvent(elementType, uuid, rule));
						};
					});
				} else if (strategyElement instanceof JComboBox<?>) {
					JComboBox<?> box = (JComboBox<?>) strategyElement;
					box.addActionListener(e -> EventBus.getInstance().fireEvent(new RuleElementChangeEvent(elementType, uuid, rule)));
				} else {
					LOG.error("Not implemented component type found during addition of listeners: {}", strategyElement.getClass().getName());
				}
				break;
			case "text":
				// ignore text changes since they should not occur
				break;
			default:
				LOG.error("Invalid rule element specific key: {}", annotation.getRuleElementSpecificKey());
			}

			counter++;
		}
	}

	private void autoMapItemElementsToDifferentRepositoryByName(SharedRule rule, ItemDirectory targetDirectory) {
		// map actions
		rule.getActions().forEach(a -> {
			final Map<ActionTypeSpecificKey, SharedItem> itemsToReplace = new HashMap<>();
			a.getActionTypeContainer().getActionTypeSpecificValues().entrySet().forEach(e -> {
				if (e.getValue() instanceof SharedItem) {
					final String itemName = ((SharedItem) e.getValue()).getName();
					SharedItem item = targetDirectory.getItemByName(itemName);
					if (itemName != null) {
						itemsToReplace.put(e.getKey(), item);
					}
				}
			});
			a.getActionTypeContainer().getActionTypeSpecificValues().putAll(itemsToReplace);
		});

		// map conditions
		rule.getConditions().forEach(a -> {
			final Map<ConditionTypeSpecificKey, SharedItem> itemsToReplace = new HashMap<>();
			a.getConditionTypeContainer().getConditionTypeSpecificValues().entrySet().forEach(e -> {
				if (e.getValue() instanceof SharedItem) {
					final String itemName = ((SharedItem) e.getValue()).getName();
					SharedItem item = targetDirectory.getItemByName(itemName);
					if (itemName != null) {
						itemsToReplace.put(e.getKey(), item);
					}
				}
			});
			a.getConditionTypeContainer().getConditionTypeSpecificValues().putAll(itemsToReplace);
		});

		// map triggers
		rule.getTriggers().forEach(a -> {
			final Map<TriggerTypeSpecificKey, SharedItem> itemsToReplace = new HashMap<>();
			a.getTriggerTypeContainer().getTriggerTypeSpecificValues().entrySet().forEach(e -> {
				if (e.getValue() instanceof SharedItem) {
					final String itemName = ((SharedItem) e.getValue()).getName();
					SharedItem item = targetDirectory.getItemByName(itemName);
					if (itemName != null) {
						itemsToReplace.put(e.getKey(), item);
					}
				}
			});
			a.getTriggerTypeContainer().getTriggerTypeSpecificValues().putAll(itemsToReplace);
		});

		EventBus.getInstance().fireEvent(new SelectedRuleChangeEvent(rule));
	}

	public boolean validateForPlatformExport(ItemDirectory targetItemDirectory) {
		autoMapItemElementsToDifferentRepositoryByName(rule, targetItemDirectory);
		final Iterable<SharedItem> items = aggregateItemsFromRuleWithDuplicates(rule);
		boolean result = true;
		for (SharedItem item : items) {
			ServerConnection serverConnection = ItemDirectoryHolder.getInstance().getServerConnection(item);
			if (serverConnection.getPlatformType() == PlatformType.MONGO) {
				result = false;
				itemElements.get(item).stream()
						.filter(c -> ItemDirectoryHolder.getInstance().getServerConnection((SharedItem) c.getSelectedItem()).getPlatformType() == PlatformType.MONGO)
						.forEach(c -> c.setBorder(BorderFactory.createEtchedBorder(Color.RED, Color.BLACK)));
			}
		}
		return result;
	}

}
