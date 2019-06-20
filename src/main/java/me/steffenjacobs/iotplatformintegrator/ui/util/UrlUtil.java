package me.steffenjacobs.iotplatformintegrator.ui.util;

import org.apache.commons.lang3.tuple.Pair;

/** @author Steffen Jacobs */
public class UrlUtil {
	public static Pair<String, Integer> parseUrlWithPort(String urlWithPort) {
		final String port;
		int lastColonIndex = urlWithPort.lastIndexOf(":");
		if (lastColonIndex == -1) {
			port = "80";
		} else {
			port = urlWithPort.substring(lastColonIndex + 1, urlWithPort.length());
		}

		int portN = 80;
		try {
			portN = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			// nothing to do since port is already set to default port 80.
			return Pair.of(urlWithPort, portN);
		}
		return Pair.of(urlWithPort.substring(0, lastColonIndex), portN);
	}
}
