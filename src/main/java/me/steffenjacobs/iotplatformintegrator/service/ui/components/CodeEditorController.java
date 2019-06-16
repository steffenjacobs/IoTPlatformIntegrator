package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.shared.PseudocodeGenerator;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;

/** @author Steffen Jacobs */
public class CodeEditorController {

	private static final PseudocodeGenerator pseudocodeGenerator = new PseudocodeGenerator();
	private final CodeEditor codeEditor;

	private final ArrayList<Token> tokens = new ArrayList<>();

	public CodeEditorController(CodeEditor codeEditor) {
		this.codeEditor = codeEditor;
	}

	public void renderPseudocode(SharedRule rule) {
		clear();
		List<Token> generatedTokens = pseudocodeGenerator.generateCodeForRule(rule);
		for (Token t : generatedTokens) {
			appendToken(t);
		}
	}

	private void clear() {
		codeEditor.clear();
		tokens.clear();
	}

	private void appendToken(Token t) {

		// clean whitespace tokens
		if (!t.getText().equals("") && !t.getText().equals(" ")) {
			tokens.add(t);
		}
		if (t instanceof ReferenceToken) {
			return;
		}

		final Color color = determineColorForTokenType(t.getTokenType());
		codeEditor.appendToPane(t.getText(), color);
		
		//append space
		codeEditor.appendToPane(" ", color);
	}

	public String getTooltipForTokenByIndex(int tokenIndex) {
		return tokens.get(tokenIndex).getTooltip();
	}

	private Color determineColorForTokenType(TokenType t) {
		final Color cl;
		switch (t) {
		case KEYWORD:
			cl = Color.BLUE;
			break;
		case ITEM:
		case COMMAND:
			cl = Color.RED;
			break;
		case UNKNOWN:
			cl = Color.CYAN;
			break;
		case VALUE:
		case OPERATOR:
		default:
			cl = Color.BLACK;
		}
		return cl;
	}

	public static class Token {
		private final String text;
		private final TokenType tokenType;
		private final String tooltip;

		public Token(String text, TokenType tokenType, String tooltip) {
			super();
			this.text = text;
			this.tokenType = tokenType;
			this.tooltip = tooltip;
		}

		public String getText() {
			return text;
		}

		public TokenType getTokenType() {
			return tokenType;
		}

		public String getTooltip() {
			return tooltip;
		}

		public static enum TokenType {
			KEYWORD, ITEM, COMMAND, VALUE, OPERATOR, UNCLASSIFIED, UNKNOWN;
		}
	}

	public static class ReferenceToken extends Token {
		public ReferenceToken(Token t) {
			super(t.getText(), t.getTokenType(), t.getTooltip());
		}
	}

}
