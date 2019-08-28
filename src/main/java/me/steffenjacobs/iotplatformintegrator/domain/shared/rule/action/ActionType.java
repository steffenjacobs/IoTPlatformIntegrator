package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

/** @author Steffen Jacobs */
public enum ActionType implements SharedElementType {

	EnableDisableRule(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.Enable, ActionTypeSpecificKey.RuleUUIDs }), //
	ExecuteScript(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.Type, ActionTypeSpecificKey.Script }), //
	PlaySound(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.Sink, ActionTypeSpecificKey.Sound }), //
	RunRules(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.RuleUUIDs, ActionTypeSpecificKey.ConsiderConditions }), //
	SaySomething(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.Sink, ActionTypeSpecificKey.Text }), //
	ItemCommand(new ActionTypeSpecificKey[] { ActionTypeSpecificKey.ItemName, ActionTypeSpecificKey.Command }), //
	Unknown(new ActionTypeSpecificKey[0]);

	private final ActionTypeSpecificKey[] typeSpecificKeys;

	private ActionType(ActionTypeSpecificKey... typeSpecificKeys) {
		this.typeSpecificKeys = typeSpecificKeys;
	}

	public ActionTypeSpecificKey[] getTypeSpecificKeys() {
		return typeSpecificKeys;
	}

	public static enum ActionTypeSpecificKey implements SharedTypeSpecificKey {
		Enable("enable", "Enable"), RuleUUIDs("ruleUIDs", "Rule UUIDs"), Type("type", "Type"), Script("script", "Script"), Sink("sink", "Sink"), Sound("sound",
				"Sound"), ConsiderConditions("considerConditions", "Consider conditions"), Text("text", "Text"), ItemName("itemName", "Item Name"), Command("command", "Command");

		private final String keyString;
		private final String displayString;

		private ActionTypeSpecificKey(String keyString, String displayString) {
			this.keyString = keyString;
			this.displayString = displayString;
		}

		public String getDisplayString() {
			return displayString;
		}

		public String getKeyString() {
			return keyString;
		}
	}

	@Override
	public String getType() {
		return ACTION_TYPE;
	}

	public static ActionType[] acceptableValues() {
		return new ActionType[] { ItemCommand, EnableDisableRule, ExecuteScript, PlaySound, RunRules, SaySomething };
	}
}
