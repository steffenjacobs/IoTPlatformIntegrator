package me.steffenjacobs.iotplatformintegrator.domain.manage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedElementType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.UnknownSharedElementType;

/** @author Steffen Jacobs */

public class SharedRuleElementDiff {
	private final UUID uid;
	private final String description;
	private final String label;
	private final SharedElementType elementType;
	private final Map<String, Object> propertiesAdded = new HashMap<>();
	private final Map<String, Object> propertiesRemoved = new HashMap<>();
	private final Map<String, Object> propertiesUpdated = new HashMap<>();
	private final boolean negative;
	private final int relativeElementId;
	private final Collection<DiffType> diffTypes;

	private Optional<SharedRule> sourceRule = Optional.empty();
	private Optional<String> targetRuleName = Optional.empty();
	private Optional<SharedRuleElementDiff> prevDiff = Optional.empty();

	public SharedRuleElementDiff(String description, String label, SharedElementType elementType, Map<String, Object> propertiesAdded, Map<String, Object> propertiesRemoved,
			Map<String, Object> propertiesUpdated, boolean isNegative, int relativeElementId, Collection<DiffType> diffTypes) {
		this(UUID.randomUUID(), description, label, elementType, propertiesAdded, propertiesRemoved, propertiesUpdated, isNegative, relativeElementId, diffTypes);
	}

	public SharedRuleElementDiff(UUID uid, String description, String label, SharedElementType elementType, Map<String, Object> propertiesAdded,
			Map<String, Object> propertiesRemoved, Map<String, Object> propertiesUpdated, boolean isNegative, int relativeElementId, Collection<DiffType> diffTypes) {
		super();
		this.uid = uid;
		this.description = description;
		this.label = label;
		this.elementType = elementType;
		this.relativeElementId = relativeElementId;
		if (propertiesAdded != null) {
			this.propertiesAdded.putAll(propertiesAdded);
		}
		if (propertiesRemoved != null) {
			this.propertiesRemoved.putAll(propertiesRemoved);
		}
		if (propertiesUpdated != null) {
			this.propertiesUpdated.putAll(propertiesUpdated);
		}
		this.negative = isNegative;
		this.diffTypes = diffTypes;
	}

	public void setTargetRuleName(String targetRuleName) {
		this.targetRuleName = Optional.of(targetRuleName);
	}

	public void setPrevDiff(SharedRuleElementDiff prevDiff) {
		this.prevDiff = Optional.of(prevDiff);
	}

	public void setSourceRule(SharedRule sourceRule) {
		this.sourceRule = Optional.of(sourceRule);
	}

	/** Constructor to create an empty diff element. */
	public SharedRuleElementDiff(boolean isNegative, int relativeElementId) {
		this(null, null, UnknownSharedElementType.INSTANCE, null, null, null, isNegative, relativeElementId, Collections.singleton(DiffType.FULL));
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

	/**
	 * @return true: if rule element should be deleted<br/>
	 *         false: else
	 */
	public boolean isNegative() {
		return negative;
	}

	public int getRelativeElementId() {
		return relativeElementId;
	}

	public UUID getUid() {
		return uid;
	}

	public Optional<String> getTargetRule() {
		return targetRuleName;
	}

	public Optional<SharedRuleElementDiff> getPrevDiff() {
		return prevDiff;
	}

	public Optional<SharedRule> getSourceRule() {
		return sourceRule;
	}

	public Collection<DiffType> getDiffTypes() {
		return diffTypes;
	}
}
