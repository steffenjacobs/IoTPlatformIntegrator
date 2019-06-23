package me.steffenjacobs.iotplatformintegrator.service.manage.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public class PseudoCodeRenderingStrategy implements RenderingStrategy<Token> {
	
	private final TokenType defaultTextType;

	public PseudoCodeRenderingStrategy(TokenType defaultTextType) {
		this.defaultTextType = defaultTextType;
		
	}

	@Override
	public Token operationComponent(Operation operation, SharedTypeSpecificKey key) {
		return new Token(operation.getText(), TokenType.OPERATOR, String.format("Type: %s, Operator: %s (%s)", TokenType.OPERATOR, operation.getText(), operation.name()));
	}

	@Override
	public Token commandComponent(Command command, SharedTypeSpecificKey key) {
		return new Token(command.name(), TokenType.COMMAND, String.format("Type: %s, Command: %s", TokenType.COMMAND, command.name()));
	}

	@Override
	public List<Token> valueComponent(String value, SharedTypeSpecificKey key) {
		if (value.contains(" ")) {
			return multivalueComponent(value);
		}
		return Arrays.asList(new Token(value, TokenType.VALUE, String.format("Type: %s, Value: %s", TokenType.VALUE, value)));
	}

	@Override
	public Token textComponent(String text, String description, TokenType type) {
		return new Token(text, type, description);
	}

	@Override
	public Token itemComponent(SharedItem item, SharedTypeSpecificKey key) {
		return new Token(item.getName(), TokenType.ITEM, String.format("Type: %s, Name: %s (%s)", TokenType.ITEM, item.getName(), item.getLabel()));
	}

	public Token unknownToken(String message) {
		return new Token(message, TokenType.UNKNOWN, message);
	}

	private List<Token> multivalueComponent(String value) {
		String[] values = value.trim().split(" ");
		List<Token> tokens = new ArrayList<>();
		for (String val : values) {
			tokens.add(new Token(val, TokenType.MULTI_VALUE, String.format("Type: %s, Value: %s", TokenType.MULTI_VALUE, value.trim())));
		}
		return tokens;
	}

	@Override
	public Token textComponent(String text, String description) {
		return textComponent(text, description, defaultTextType);
	}
}
