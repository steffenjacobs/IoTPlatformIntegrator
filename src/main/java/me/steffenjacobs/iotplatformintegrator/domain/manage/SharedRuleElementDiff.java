package me.steffenjacobs.iotplatformintegrator.domain.manage;

import java.util.HashMap;
import java.util.Map;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;

/** @author Steffen Jacobs */

public class SharedRuleElementDiff {
	private final String description;
	private final String label;
	private final SharedElementType elementType;
	private final Map<String, Object> propertiesAdded = new HashMap<>();
	private final Map<String, Object> propertiesRemoved = new HashMap<>();
	private final Map<String, Object> propertiesUpdated = new HashMap<>();
	private final boolean negative;

	public SharedRuleElementDiff(String description, String label, SharedElementType elementType, Map<String, Object> propertiesAdded, Map<String, Object> propertiesRemoved,
			Map<String, Object> propertiesUpdated, boolean isNegative) {
		super();
		this.description = description;
		this.label = label;
		this.elementType = elementType;
		this.propertiesAdded.putAll(propertiesAdded);
		this.propertiesRemoved.putAll(propertiesRemoved);
		this.propertiesUpdated.putAll(propertiesUpdated);
		this.negative = isNegative;
	}

	/** Constructor to create an empty diff element. */
	public SharedRuleElementDiff(boolean isNegative) {
		this.description = null;
		this.label = null;
		this.elementType = null;
		this.negative = isNegative;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public SharedElementType getElementType() {
		return elementType;
	}

	public Map<String, Object> getPropertiesAdded() {
		return propertiesAdded;
	}

	public Map<String, Object> getPropertiesRemoved() {
		return propertiesRemoved;
	}

	public Map<String, Object> getPropertiesUpdated() {
		return propertiesUpdated;
	}

	public boolean isEmpty() {
		return description == null && label == null && elementType == null && propertiesAdded.isEmpty() && propertiesRemoved.isEmpty() && propertiesUpdated.isEmpty();
	}

	public boolean isNegative() {
		return negative;
	}

}
