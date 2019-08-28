package me.steffenjacobs.iotplatformintegrator.domain.shared.rule.trigger;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedTypeSpecificKey;

/** @author Steffen Jacobs */
public enum TriggerType implements SharedElementType {

	ItemStateChanged(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.PreviousState, TriggerTypeSpecificKey.State }), //
	CommandReceived(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.Command }), //
	ItemStateUpdated(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.ItemName, TriggerTypeSpecificKey.State }), //
	Timed(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.Time }), //
	TriggerChannelFired(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.Channel, TriggerTypeSpecificKey.Event, TriggerTypeSpecificKey.EventData }), //
	GeoLocationChanged(new TriggerTypeSpecificKey[] { TriggerTypeSpecificKey.Event, TriggerTypeSpecificKey.EventData, TriggerTypeSpecificKey.ItemName }), //
	Unknown(new TriggerTypeSpecificKey[0]);

	private final TriggerTypeSpecificKey[] typeSpecificKeys;

	private TriggerType(TriggerTypeSpecificKey... typeSpecificKeys) {
		this.typeSpecificKeys = typeSpecificKeys;
	}

	public TriggerTypeSpecificKey[] getTypeSpecificKeys() {
		return typeSpecificKeys;
	}

	public static enum TriggerTypeSpecificKey implements SharedTypeSpecificKey {
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

	@Override
	public String getType() {
		return TRIGGER_TYPE;
	}

	public static TriggerType[] acceptableValues() {
		return new TriggerType[] { ItemStateChanged, CommandReceived, ItemStateUpdated, Timed, TriggerChannelFired, GeoLocationChanged };
	}
}
