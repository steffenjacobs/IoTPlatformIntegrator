package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.condition;

/** @author Steffen Jacobs */
public enum ConditionType {
	ScriptEvaluatesTrue(new ConditionTypeSpecificKey[] { ConditionTypeSpecificKey.Script, ConditionTypeSpecificKey.Type }), //
	ItemState(new ConditionTypeSpecificKey[] { ConditionTypeSpecificKey.ItemName, ConditionTypeSpecificKey.State, ConditionTypeSpecificKey.Operator }), //
	DayOfWeek(new ConditionTypeSpecificKey[] {}), //
	TimeOfDay(new ConditionTypeSpecificKey[] { ConditionTypeSpecificKey.StartTime, ConditionTypeSpecificKey.EndTime }), //
	Unknown(new ConditionTypeSpecificKey[0]);

	private final ConditionTypeSpecificKey[] typeSpecificKeys;

	private ConditionType(ConditionTypeSpecificKey... typeSpecificKeys) {
		this.typeSpecificKeys = typeSpecificKeys;
	}

	public ConditionTypeSpecificKey[] getTypeSpecificKeys() {
		return typeSpecificKeys;
	}

	public static enum ConditionTypeSpecificKey {
		Script("script", "Script"), Type("type", "Type"), ItemName("itemName", "Item Name"), State("state", "State"), Operator("operator", "Operator"), StartTime("startTime",
				"Start Time"), EndTime("endTime", "End Time");

		private final String keyString;
		private final String displayString;

		private ConditionTypeSpecificKey(String keyString, String displayString) {
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
}
