package me.steffenjacobs.iotplatformintegrator.domain.shared.item;

import org.apache.commons.lang3.ArrayUtils;

/** @author Steffen Jacobs */
/**
 * An ItemType is the type of an item. Each item type has a fixed data type and
 * a fixed set of allowed commands. The data type implies a fixed set of allowed
 * operations.
 */
public enum ItemType {
	Switch(new DataType[] { DataType.Discrete }, Command.getSwitchCommands()), Contact(new DataType[] { DataType.Discrete }, Command.getContactCommands()), //
	String(new DataType[] { DataType.Discrete }, new Command[] { Command.String }), Number(new DataType[] { DataType.Numerical }, new Command[] { Command.Decimal }), //
	Dimmer(new DataType[] { DataType.Numerical }, Command.getDimmerCommands()), DateTime(new DataType[] { DataType.Numerical }, new Command[0]), //
	Color(new DataType[] { DataType.Numerical }, Command.getColorCommands()), Image(new DataType[] { DataType.Discrete }, new Command[0]), //
	Player(new DataType[] { DataType.Discrete }, Command.getPlayerCommands()), Location(new DataType[] { DataType.Discrete }, new Command[] { Command.Point }), //
	Rollershutter(new DataType[] { DataType.Numerical }, Command.getRollershutterCommands()), //
	Group(new DataType[] { DataType.Discrete }, new Command[0]), Unknown(new DataType[] { DataType.Unknown }, new Command[0]), //
	AUTOMATION_RULE(new DataType[] { DataType.Unknown }, new Command[0]);

	private final DataType datatype;
	private final Command[] commands;

	private ItemType(DataType[] datatypes, Command[] commands) {
		this.datatype = datatypes[0];
		this.commands = commands;
	}

	public Command[] getAllowedCommands() {
		return commands;
	}

	public DataType getDatatype() {
		return datatype;
	}

	/**
	 * Defines the data type of an item as either discrete or numerical. Each data
	 * type implies its own allowed operations.
	 */
	enum DataType {
		Discrete(Operation.EQUAL, Operation.NOT_EQUAL), Numerical(Operation.EQUAL, Operation.SMALLER_EQUAL, Operation.SMALLER, Operation.BIGGER_EQUAL, Operation.BIGGER,
				Operation.NOT_EQUAL), Unknown;

		private final Operation[] operations;

		private DataType(Operation... operations) {
			this.operations = operations;
		}

		public Operation[] getOperations() {
			return operations;
		}
	}

	/** Commands to execute on the items. */
	public enum Command {
		On, Off, Open, Closed, String, Decimal, Increase, Decrease, Percent, HSB, Point, //
		Play, Pause, Next, Previous, Rewind, Fastforward, //
		Up, Down, StopMove, Unknown;

		static Command[] getColorCommands() {
			return ArrayUtils.addAll(getDimmerCommands(), new Command[] { HSB });
		}

		static Command[] getDimmerCommands() {
			return ArrayUtils.addAll(getSwitchCommands(), new Command[] { Increase, Decrease, Percent });
		}

		static Command[] getSwitchCommands() {
			return new Command[] { On, Off };
		}

		static Command[] getContactCommands() {
			return new Command[] { Open, Closed };
		}

		static Command[] getPlayerCommands() {
			return new Command[] { Play, Pause, Next, Previous, Rewind, Fastforward };
		}

		static Command[] getRollershutterCommands() {
			return new Command[] { Up, Down, StopMove, Percent };
		}

		public static Command parse(String command) {
			switch (command.toLowerCase()) {
			case "on":
			case "turn_on":
				return On;
			case "off":
			case "turn_off":
				return Off;
			case "open":
			case "opened":
				return Open;
			case "close":
			case "closed":
				return Closed;
			case "string":
				return String;
			case "decimal":
			case "number":
			case "numerical":
				return Decimal;
			case "increase":
				return Increase;
			case "decrease":
				return Decrease;
			case "percent":
				return Percent;
			case "hsb":
				return HSB;
			case "point":
				return Point;
			case "play":
				return Play;
			case "pause":
				return Pause;
			case "next":
				return Next;
			case "previous":
				return Previous;
			case "rewind":
				return Rewind;
			case "fastforward":
				return Fastforward;
			case "up":
				return Up;
			case "down":
				return Down;
			case "StopMove":
				return StopMove;
			default:
				return Unknown;
			}
		}
	}

	/** Operations to execute with the items. */
	public enum Operation {
		EQUAL("=="), SMALLER_EQUAL("\u2264"), SMALLER("<"), BIGGER_EQUAL("\u2265"), BIGGER(">"), NOT_EQUAL("\u2260"), OR("\u2228"), AND("\u2227"), UNKNOWN("<?>");

		final String text;

		private Operation(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public static boolean isOrOrAnd(String text) {
			switch (text) {
			case "\u2228":
			case "\u2227":
				return true;
			default:
				return false;
			}
		}

		public static Object fromText(String text) {
			switch (text) {
			case "==":
				return EQUAL;
			case "\u2264":
				return SMALLER_EQUAL;
			case "<":
				return SMALLER;
			case ">":
				return BIGGER;
			case "\u2265":
				return BIGGER_EQUAL;
			case "\u2260":
				return NOT_EQUAL;
			case "\u2228":
				return OR;
			case "\u2227":
				return AND;
			default:
				return UNKNOWN;
			}
		}

		public static Object fromString(String value) {
			switch (value) {
			case "=":
				return EQUAL;
			case "<=":
				return SMALLER_EQUAL;
			case "<":
				return SMALLER;
			case ">":
				return BIGGER;
			case ">=":
				return BIGGER_EQUAL;
			case "\u2260":
				return NOT_EQUAL;
			case "\u2228":
				return OR;
			case "\u2227":
				return AND;
			default:
				return UNKNOWN;
			}
		}
	}
}
