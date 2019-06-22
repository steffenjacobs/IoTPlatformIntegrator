package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;

import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public interface RenderingStrategy<ComponentType> {

	ComponentType operationComponent(Operation operation);

	ComponentType commandComponent(Command command);

	List<ComponentType> valueComponent(String value);

	ComponentType textComponent(String text, String description, TokenType type);

	ComponentType textComponent(String text, String description);

	ComponentType itemComponent(SharedItem item);

}