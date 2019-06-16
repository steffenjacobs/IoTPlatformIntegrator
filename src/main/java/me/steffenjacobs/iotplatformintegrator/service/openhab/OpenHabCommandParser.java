package me.steffenjacobs.iotplatformintegrator.service.openhab;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;

/** @author Steffen Jacobs */
public class OpenHabCommandParser {

	public ItemType.Command parseCommand(String command) {
		switch (command) {
		case "ON":
			return Command.On;
		case "OFF":
			return Command.Off;
		case "OPEN":
			return Command.Open;
		case "CLOSE":
			return Command.Closed;
		case "STRING":
			return Command.String;
		case "DECIMAL":
			return Command.Decimal;
		case "INCREASE":
			return Command.Increase;
		case "DECREASE":
			return Command.Decrease;
		case "PERCENT":
			return Command.Percent;
		case "HSB":
			return Command.HSB;
		case "POINT":
			return Command.Point;
		case "PLAY":
			return Command.Play;
		case "PAUSE":
			return Command.Pause;
		case "NEXT":
			return Command.Next;
		case "PREVIOUS":
			return Command.Previous;
		case "REWIND":
			return Command.Rewind;
		case "FASTFORWARD":
			return Command.Fastforward;
		case "UP":
			return Command.Up;
		case "DOWN":
			return Command.Down;
		case "STOPMOVE":
			return Command.StopMove;
		default:
			return Command.Unknown;
		}
	}
}
