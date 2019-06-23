package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public interface RenderingStrategy<ComponentType> {

	ComponentType operationComponent(Operation operation, SharedTypeSpecificKey key);

	ComponentType commandComponent(Command command, SharedTypeSpecificKey key);

	List<ComponentType> valueComponent(String value, SharedTypeSpecificKey key);

	ComponentType textComponent(String text, String description, TokenType type);

	ComponentType textComponent(String text, String description);

	ComponentType itemComponent(SharedItem item, SharedTypeSpecificKey key);

}
