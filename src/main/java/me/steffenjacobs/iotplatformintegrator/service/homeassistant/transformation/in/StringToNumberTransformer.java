package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in;

import me.steffenjacobs.iotplatformintegrator.service.manage.util.StringUtil;

/** @author Steffen Jacobs */
public class StringToNumberTransformer {
	public Object returnNumberIfPossibleElseString(String value) {
		if (!StringUtil.isNonNull(value)) {
			return null;
		}
		double result;
		try {
			result = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return value;
		}

		if ((result == Math.floor(result)) && !Double.isInfinite(result)) {
			return (long) result;
		}
		return result;
	}
}
