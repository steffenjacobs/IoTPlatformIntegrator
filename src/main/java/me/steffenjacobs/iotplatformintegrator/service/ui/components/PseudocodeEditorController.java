package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedRuleChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.shared.PseudocodeGenerator;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.PseudocodeEditorController.Token.TokenType;
import me.steffenjacobs.iotplatformintegrator.ui.components.PseudocodeEditor;

/** @author Steffen Jacobs */
public class PseudocodeEditorController {

	private static final PseudocodeGenerator pseudocodeGenerator = new PseudocodeGenerator();
	private final PseudocodeEditor codeEditor;
	private final SettingService settingService;

	private final ArrayList<Token> tokens = new ArrayList<>();
	private SharedRule rule = null;

	public PseudocodeEditorController(PseudocodeEditor codeEditor, SettingService settingService) {
		this.codeEditor = codeEditor;
		this.settingService = settingService;

		EventBus.getInstance().addEventHandler(EventType.SELECTED_RULE_CHANGE, e -> renderPseudoCode(((SelectedRuleChangeEvent) e).getSelectedRule()));

		EventBus.getInstance().addEventHandler(EventType.RULE_CHANGE, e -> {
			RuleChangeEvent event = (RuleChangeEvent) e;
			if (event.getSelectedRule() == rule) {
				renderPseudoCode(event.getSelectedRule());
			}
		});
	}

	private void renderPseudoCode(SharedRule rule) {
		this.rule = rule;
		if (rule == null) {
			codeEditor.showHelpText();
		}
		clear();
		List<Token> generatedTokens = pseudocodeGenerator.generateCodeForRule(rule);
		renderFormatted(generatedTokens);
	}

	private void renderFormatted(List<Token> generatedTokens) {
		String prefix = "";
		String suffix = "";
		boolean firstKeyword = true;
		for (int i = 0; i < generatedTokens.size(); i++) {
			Token t = generatedTokens.get(i);
			if ("1".equals(settingService.getSetting(SettingKey.FORMAT_CODE))) {
				if (t.getTokenType() == TokenType.KEYWORD) {
					prefix = firstKeyword ? "" : "\n\n";
					firstKeyword = false;
					suffix = "\n    ";
				} else if (t.getTokenType() == TokenType.OPERATOR && Operation.isOrOrAnd(t.getText())) {
					prefix = "\n        ";
					suffix = "";
				} else {
					suffix = "";
					prefix = "";
				}
			}
			appendToken(t, prefix, suffix);
		}
	}

	private void clear() {
		codeEditor.clear();
		tokens.clear();
	}

	private void appendToken(Token t, String prefix, String suffix) {

		// clean whitespace tokens
		if (!t.getText().equals("") && !t.getText().equals(" ")) {
			tokens.add(t);
		}
		if (t instanceof ReferenceToken) {
			return;
		}

		final Color color = determineColorForTokenType(t.getTokenType());
		final boolean bold = determineBoldnessForTokenType(t.getTokenType());
		codeEditor.appendToPane(prefix + t.getText() + suffix, color, bold);

		// append space
		codeEditor.appendToPane(" ", color, bold);
	}

	public String getTooltipForTokenByIndex(int tokenIndex) {
		return tokenIndex < tokens.size() ? tokens.get(tokenIndex).getTooltip() : "";
	}

	private boolean determineBoldnessForTokenType(TokenType t) {
		switch (t) {
		case KEYWORD:
		case ITEM:
		case COMMAND:
			return true;
		default:
			return false;
		}
	}

	private Color determineColorForTokenType(TokenType t) {
		final Color cl;
		switch (t) {
		case KEYWORD:
			cl = Color.decode("#7f0055");
			break;
		case ITEM:
		case COMMAND:
			cl = Color.decode("#0069b5");
			break;
		case UNKNOWN:
			cl = Color.RED;
			break;
		case VALUE:
		case MULTI_VALUE:
		case OPERATOR:
			cl = Color.decode("#7f9fd6");
			break;
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
			KEYWORD, ITEM, COMMAND, VALUE, OPERATOR, UNCLASSIFIED, UNKNOWN, TRIGGER_CONDITION, CONDITION, ACTION, MULTI_VALUE;
		}
	}

	public static class ReferenceToken extends Token {
		public ReferenceToken(Token t) {
			super(t.getText(), t.getTokenType(), t.getTooltip());
		}
	}

}
