package me.steffenjacobs.iotplatformintegrator.domain.shared.rule;

/** @author Steffen Jacobs */
public class UnknownSharedElementType implements SharedElementType {

	public static final UnknownSharedElementType INSTANCE = new UnknownSharedElementType();

	private UnknownSharedElementType() {
		// private constructor to ensure this is a singleton
	}

	@Override
	public String name() {
		return "unknown";
	}

	@Override
	public String getType() {
		return UNKNOWN_TYPE;
	}

}
