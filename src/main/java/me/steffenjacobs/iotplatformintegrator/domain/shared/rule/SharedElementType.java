package me.steffenjacobs.iotplatformintegrator.domain.shared.rule;

/** @author Steffen Jacobs */
public interface SharedElementType {

	static String TRIGGER_TYPE = "trigger";
	static String CONDITION_TYPE = "condition";
	static String ACTION_TYPE = "action";
	static String UNKNOWN_TYPE = "unknown";

	String name();

	String getType();
}
