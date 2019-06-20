package me.steffenjacobs.iotplatformintegrator.service.manage.util;

/** @author Steffen Jacobs */
public class StringUtil {

	public static boolean isNonNull(String s) {
		return s != null && !s.isEmpty() && !s.equals("null");
	}

}
