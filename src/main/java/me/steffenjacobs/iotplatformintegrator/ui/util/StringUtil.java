package me.steffenjacobs.iotplatformintegrator.ui.util;

/** @author Steffen Jacobs */
public class StringUtil {

	public static boolean isNonNull(String s) {
		return s != null && !s.isEmpty() && !s.equals("null");
	}

}
