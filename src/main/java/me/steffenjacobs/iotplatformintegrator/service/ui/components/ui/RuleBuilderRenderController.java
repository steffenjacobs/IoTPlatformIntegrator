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
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRuleElement;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleElementChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.WithSharedRuleEvent;
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

	private final RuleElementRecommender recommender = new RuleElementRecommender(this);
	private final RuleElementValidator validator = new RuleElementValidator(this);

	public Optional<SharedRule> getDisplayedRule() {
		return rule == null ? Optional.empty() : Optional.of(rule);
	}

	public RuleBuilderRenderController(RuleBuilder ruleBuilder) {
		new RuleMutator(this);
		this.ruleBuilder = ruleBuilder;
		ruleBuilder.setRenderController(this);

		EventBus.getInstance().addEventHandler(EventType.SelectedRuleChanged, e -> renderRule(((WithSharedRuleEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RuleChangeEvent, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				renderRule(rule);
			}
			ruleBuilder.onSelectedRuleChanged(event.getSelectedRule());
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
		SharedTrigger trigger = new SharedTrigger(oldTrigger.getTriggerTypeContainer().getTriggerType(), properties, oldTrigger.getDescription(), oldTrigger.getLabel(), oldTrigger.getRelativeElementId());
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
		SharedAction action = new SharedAction(oldAction.getActionTypeContainer().getActionType(), properties, oldAction.getDescription(), oldAction.getLabel(), oldAction.getRelativeElementId());
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
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);
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
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);
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
		recommender.addRecommendations(strategyElements);
		validator.addValidation(strategyElements);

		elem.setStrategyElements(strategyElements);
		ruleElements.put(uuid, condition);
		renderedRuleElements.put(uuid, elem);
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

}
