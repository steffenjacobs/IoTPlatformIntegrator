package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.manage.RuleRelatedAnnotation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementRemovedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedSourceRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ActionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ConditionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.TriggerRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.VisualRenderingStrategy;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ActionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.ConditionElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.DynamicElement.ElementType;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.RuleBuilder;
import me.steffenjacobs.iotplatformintegrator.ui.components.rulebuilder.TriggerElement;

/** @author Steffen Jacobs */
public class RuleBuilderRenderController implements RuleComponentRegistry {

	private static final Logger LOG = LoggerFactory.getLogger(RuleBuilderRenderController.class);

	private final TriggerRenderer<Component> triggerRenderer = new TriggerRenderer<>(new VisualRenderingStrategy(this));
	private final ConditionRenderer<Component> conditionRenderer = new ConditionRenderer<>(new VisualRenderingStrategy(this));
	private final ActionRenderer<Component> actionRenderer = new ActionRenderer<>(new VisualRenderingStrategy(this));

	private final RuleBuilder ruleBuilder;

	private final Map<UUID, DynamicElement> renderedRuleElements = new HashMap<>();
	private final Map<UUID, SharedRuleElement> ruleElements = new HashMap<>();

	private final Map<Component, RuleRelatedAnnotation> annotatedComponents = new HashMap<>();

	private SharedRule rule = null;

	public RuleBuilderRenderController(RuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;

		EventBus.getInstance().addEventHandler(EventType.SelectedSourceRuleChanged, e -> renderRule(((SelectedSourceRuleChangeEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RuleElementAdded, e -> addRuleElement(((RuleElementAddedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RuleElementRemoved, e -> removeRuleElement(((RuleElementRemovedEvent) e).getSourceId()));
		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				renderRule(rule);
			}
		});
		EventBus.getInstance().addEventHandler(EventType.RuleElementChangeEvent, e -> {
			RuleElementChangeEvent event = (RuleElementChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				SharedRuleElement elem = ruleElements.get(event.getSourceId());
				if (elem instanceof SharedTrigger) {
					rule.getTriggers().remove(elem);
					SharedTrigger trigger = parseTriggerFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), trigger);
					rule.getTriggers().add(trigger);

				} else if (elem instanceof SharedCondition) {
					rule.getConditions().remove(elem);
					SharedCondition condition = parseConditionFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), condition);
					rule.getConditions().add(condition);

				} else if (elem instanceof SharedAction) {
					rule.getActions().remove(elem);
					SharedAction action = parseActionFromView(event.getSourceId());
					ruleElements.put(event.getSourceId(), action);
					rule.getActions().add(action);
				} else {
					LOG.error("Invalid element type: " + elem);
				}
				EventBus.getInstance().fireEvent(new RuleChangeEvent(rule));
			}
		});
	}

	private SharedTrigger parseTriggerFromView(UUID source) {
		SharedTrigger oldTrigger = (SharedTrigger) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedTrigger trigger = new SharedTrigger(oldTrigger.getTriggerTypeContainer().getTriggerType(), properties, oldTrigger.getDescription(), oldTrigger.getLabel());
		return trigger;
	}

	private SharedCondition parseConditionFromView(UUID source) {
		SharedCondition oldCondition = (SharedCondition) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedCondition condition = new SharedCondition(oldCondition.getConditionTypeContainer().getConditionType(), properties, oldCondition.getDescription(),
				oldCondition.getLabel());
		return condition;
	}

	private SharedAction parseActionFromView(UUID source) {
		SharedAction oldAction = (SharedAction) ruleElements.get(source);
		final Map<String, Object> properties = parsePropertiesFromView(renderedRuleElements.get(source));
		SharedAction action = new SharedAction(oldAction.getActionTypeContainer().getActionType(), properties, oldAction.getDescription(), oldAction.getLabel());
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
			case "value":
			case "enable":
			case "ruleUIDs":
			case "type":
			case "script":
			case "sink":
			case "sound":
			case "state":
			case "startTime":
			case "endTime":
			case "time":
			case "considerConditions":
			case "previous_state":
			case "channel":
			case "event":
			case "event_data":
				JTextField text = (JTextField) strategyElement;
				properties.put(annotation.getRuleElementSpecificKey().getKeyString(), text.getText());
				break;
			case "text":
				// ignore text changes since they should not occur
				break;
			default:
				LOG.info("Invalid rule element specific key for parsing from view: {}", annotation.getRuleElementSpecificKey());
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

	private void addRuleElement(UUID sourceId) {
		if (rule != null) {
			SharedRuleElement elem = ruleElements.get(sourceId);
			if (elem instanceof SharedTrigger) {
				rule.getTriggers().add(copy((SharedTrigger) elem));
			} else if (elem instanceof SharedCondition) {
				rule.getConditions().add(copy((SharedCondition) elem));
			} else if (elem instanceof SharedAction) {
				rule.getActions().add(copy((SharedAction) elem));
			}
			EventBus.getInstance().fireEvent(new RuleChangeEvent(rule));
		}
	}

	private SharedTrigger copy(SharedTrigger trigger) {
		Map<String, Object> properties = new HashMap<>();
		trigger.getTriggerTypeContainer().getTriggerTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedTrigger(trigger.getTriggerTypeContainer().getTriggerType(), properties, trigger.getDescription(), trigger.getLabel() + " - Copy");
	}

	private SharedCondition copy(SharedCondition condition) {
		Map<String, Object> properties = new HashMap<>();
		condition.getConditionTypeContainer().getConditionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedCondition(condition.getConditionTypeContainer().getConditionType(), properties, condition.getDescription(), condition.getLabel() + " - Copy");
	}

	private SharedAction copy(SharedAction action) {
		Map<String, Object> properties = new HashMap<>();
		action.getActionTypeContainer().getActionTypeSpecificValues().entrySet().stream().forEach(e -> properties.put(e.getKey().getKeyString(), e.getValue()));
		return new SharedAction(action.getActionTypeContainer().getActionType(), properties, action.getDescription(), action.getLabel() + " - Copy");
	}

	private void removeRuleElement(UUID sourceId) {
		if (rule != null) {
			SharedRuleElement elem = ruleElements.remove(sourceId);
			if (elem instanceof SharedTrigger) {
				SharedTrigger trigger = (SharedTrigger) elem;
				rule.getTriggers().remove(trigger);
			} else if (elem instanceof SharedCondition) {
				SharedCondition condition = (SharedCondition) elem;
				rule.getConditions().remove(condition);
			} else if (elem instanceof SharedAction) {
				SharedAction action = (SharedAction) elem;
				rule.getActions().remove(action);
			}
			EventBus.getInstance().fireEvent(new RuleChangeEvent(rule));
		}
	}

	public void renderRule(SharedRule rule) {
		this.rule = rule;
		renderedRuleElements.clear();
		ruleBuilder.clear();
		ruleElements.clear();
		annotatedComponents.clear();
		if (rule == null) {
			return;
		}
		ruleBuilder.setHeader(rule.getName(), rule.getStatus(), rule.getDescription());

		for (SharedTrigger trigger : rule.getTriggers()) {
			ruleBuilder.appendDynamicElement(renderTrigger(trigger, triggerRenderer));
		}
		for (SharedCondition condition : rule.getConditions()) {
			ruleBuilder.appendDynamicElement(renderCondition(condition, conditionRenderer));
		}
		for (SharedAction action : rule.getActions()) {
			ruleBuilder.appendDynamicElement(renderAction(action, actionRenderer));
		}
	}

	private DynamicElement renderTrigger(SharedTrigger trigger, TriggerRenderer<Component> triggerRenderer) {
		UUID uuid = UUID.randomUUID();
		TriggerElement elem = new TriggerElement(uuid);
		elem.setTriggerTypeContainer(trigger.getTriggerTypeContainer());

		String label = trigger.getLabel();
		String description = trigger.getDescription();
		elem.setToolTipText(String.format("%s: %s", label, description));

		Collection<Component> strategyElements = triggerRenderer.renderTrigger(trigger);
		addComponentListeners(uuid, strategyElements, ElementType.Condition);
		addRecommendations(strategyElements);
		elem.setStrategyElements(strategyElements);
		ruleElements.put(uuid, trigger);
		renderedRuleElements.put(uuid, elem);
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
		addComponentListeners(uuid, strategyElements, ElementType.Action);
		addRecommendations(strategyElements);
		elem.setStrategyElements(strategyElements);
		renderedRuleElements.put(uuid, elem);
		ruleElements.put(uuid, action);
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

		addComponentListeners(uuid, strategyElements, ElementType.Condition);
		addRecommendations(strategyElements);

		elem.setStrategyElements(strategyElements);
		ruleElements.put(uuid, condition);
		renderedRuleElements.put(uuid, elem);
		return elem;
	}

	@SuppressWarnings("unchecked")
	private void addRecommendations(Collection<Component> strategyElements) {
		Optional<Component> cOperator = getType(strategyElements, "operator");
		Optional<Component> cItem = getType(strategyElements, "itemName");
		Optional<Component> cState = getType(strategyElements, "state");
		Optional<Component> cCommand = getType(strategyElements, "command");

		if (cItem.isPresent()) {
			final Optional<SharedItem> item;
			if (cItem.get() instanceof JComboBox<?>) {
				SharedItem si = (SharedItem) ((JComboBox<?>) cItem.get()).getSelectedItem();
				if (si != null) {
					item = Optional.of(si);
				} else {
					item = Optional.empty();
				}
			} else {
				item = Optional.empty();
			}
			if (item.isPresent() && cCommand.isPresent()) {
				for (Command command : item.get().getType().getAllowedCommands()) {
					((JComboBox<Command>) cCommand.get()).addItem(command);
				}
			}
			if (cState.isPresent()) {
				// TODO: validation if state is a command or item
			}
			if (item.isPresent() && cOperator.isPresent()) {
				for (Operation op : item.get().getType().getDatatype().getOperations()) {
					((JComboBox<Operation>) cOperator.get()).addItem(op);
				}
			}
		}
	}

	private Optional<Component> getType(Collection<Component> components, String keyString) {
		for (Component comp : components) {
			RuleRelatedAnnotation annotationFromComponent = getAnnotationFromComponent(comp);
			if (annotationFromComponent != null && annotationFromComponent.getRuleElementSpecificKey() != null
					&& keyString.equals(annotationFromComponent.getRuleElementSpecificKey().getKeyString())) {
				return Optional.of(comp);
			}
		}
		return Optional.empty();
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
				JTextField text = (JTextField) strategyElement;
				text.addKeyListener(new KeyAdapter() {
					public void keyReleased(java.awt.event.KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER)
							EventBus.getInstance().fireEvent(new RuleElementChangeEvent(elementType, uuid, rule));
					};
				});
				break;
			case "text":
				// ignore text changes since they should not occur
				break;
			default:
				LOG.info("Invalid rule element specific key: {}", annotation.getRuleElementSpecificKey());
			}

			counter++;
		}
	}

}
