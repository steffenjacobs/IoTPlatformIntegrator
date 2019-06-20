package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger;

/** @author Steffen Jacobs */
public enum TriggerType {

	ItemStateChanged(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.PreviousState, TriggerTypeSpecificKey.State }), //
	CommandReceived(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.Command }), //
	ItemStateUpdated(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.State }), //
	Timed(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.Time }), //
	TriggerChannelFired(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.Channel, TriggerTypeSpecificKey.Event, TriggerTypeSpecificKey.EventData }), //
	Unknown(new TriggerTypeSpecificKey[0]);

	private final TriggerTypeSpecificKey[] typeSpecificKeys;

	private TriggerType(TriggerTypeSpecificKey... typeSpecificKeys) {
		this.typeSpecificKeys = typeSpecificKeys;
	}

	public TriggerTypeSpecificKey[] getTypeSpecificKeys() {
		return typeSpecificKeys;
	}

	public static enum TriggerTypeSpecificKey {
		Command("command", "Command"), PreviousState("previous_state", "Previous State"), State("state", "State"), Channel("channel", "Channel"), Event("event",
				"Event"), EventData("event_data", "Event Data"), ItemName("itemName", "Item Name"), Time("time", "Time");

		private final String keyString;
		private final String displayString;

		private TriggerTypeSpecificKey(String keyString, String displayString) {
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
