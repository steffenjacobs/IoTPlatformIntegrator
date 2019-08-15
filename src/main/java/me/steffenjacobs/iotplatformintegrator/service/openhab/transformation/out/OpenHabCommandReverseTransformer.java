package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;

/** @author Steffen Jacobs */
public class OpenHabCommandReverseTransformer {

	public String parseCommand(Command command) {
		switch (command) {
		case On:
			return "ON";
		case Off:
			return "OFF";
		case Open:
			return "OPEN";
		case Closed:
			return "CLOSED";
		case String:
			return "STRING";
		case Decimal:
			return "DECIMAL";
		case Increase:
			return "INCREASE";
		case Decrease:
			return "DECREASE";
		case Percent:
			return "PERCENT";
		case HSB:
			return "HSB";
		case Point:
			return "POINT";
		case Play:
			return "PLAY";
		case Pause:
			return "PAUSE";
		case Next:
			return "NEXT";
		case Previous:
			return "PREVIOUS";
		case Rewind:
			return "REWIND";
		case Fastforward:
			return "FASTFORWARD";
		case Up:
			return "UP";
		case Down:
			return "DOWN";
		case StopMove:
			return "STOPMOVE";
		default:
			return "UNKNWON";
		}
	}

}
