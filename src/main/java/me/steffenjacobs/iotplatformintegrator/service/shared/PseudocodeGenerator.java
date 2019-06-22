package me.steffenjacobs.iotplatformintegrator.service.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Operation;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition.SharedCondition;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger.SharedTrigger;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ActionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ConditionRenderer;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.PseudoCodeRenderingStrategy;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.TriggerRenderer;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.ReferenceToken;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController.Token.TokenType;

/** @author Steffen Jacobs */
public class PseudocodeGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(PseudocodeGenerator.class);

	public List<Token> generateCodeForRule(SharedRule sharedRule) {
		if (sharedRule == null) {
			return Arrays.asList(new Token("Please select a rule to generate pseudocode for.", Token.TokenType.UNCLASSIFIED, ""));
		}

		final TriggerRenderer<Token> triggerRenderer = new TriggerRenderer<>(new PseudoCodeRenderingStrategy(TokenType.TRIGGER_CONDITION));
		final ConditionRenderer<Token> conditionRenderer = new ConditionRenderer<>(new PseudoCodeRenderingStrategy(TokenType.CONDITION));
		final ActionRenderer<Token> actionRenderer = new ActionRenderer<>(new PseudoCodeRenderingStrategy(TokenType.ACTION));

		List<Token> tokens = new ArrayList<>();

		// triggers
		addToken(tokens, keywordToken("WHEN"));
		int count = 0;
		for (SharedTrigger trigger : sharedRule.getTriggers()) {
			for (Token t : triggerRenderer.renderTrigger(trigger)) {
				addToken(tokens, t);
			}
			if (count < sharedRule.getTriggers().size() - 1) {
				addToken(tokens, operatorToken(Operation.OR));
			}
			count++;
		}

		// conditions
		if (!sharedRule.getConditions().isEmpty()) {

			addToken(tokens, keywordToken("IF"));
			count = 0;
			for (SharedCondition condition : sharedRule.getConditions()) {
				for (Token t : conditionRenderer.renderCondition(condition)) {
					addToken(tokens, t);
				}
				if (count < sharedRule.getConditions().size() - 1) {
					addToken(tokens, operatorToken(Operation.OR));
				}
				count++;
			}
		}

		// actions
		if (!sharedRule.getActions().isEmpty()) {

			addToken(tokens, keywordToken("DO"));
			count = 0;
			for (SharedAction action : sharedRule.getActions()) {
				for (Token t : actionRenderer.renderAction(action)) {
					addToken(tokens, t);
				}
				if (count < sharedRule.getActions().size() - 1) {
					addToken(tokens, operatorToken(Operation.AND));
				}
				count++;
			}
		}
		LOG.info("Generated {} tokens.", tokens.size());
		return tokens;
	}

	public void addToken(List<Token> tokens, Token t) {
		tokens.add(t);
		int cnt = StringUtils.countMatches(t.getText().replaceAll(" +", " "), " ");
		if (cnt > 1) {
			ReferenceToken rt = new ReferenceToken(t);
			for (int i = 0; i < cnt; i++) {
				tokens.add(rt);
			}
		}
	}

	private Token keywordToken(String keyword) {
		return new Token(keyword, TokenType.KEYWORD, "Keyword " + keyword);
	}

	private Token operatorToken(Operation operation) {
		return new Token(operation.getText(), TokenType.OPERATOR, String.format("Type: %s, Operator: %s (%s)", TokenType.OPERATOR, operation.getText(), operation.name()));
	}
}
