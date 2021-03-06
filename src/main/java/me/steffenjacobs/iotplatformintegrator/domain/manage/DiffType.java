package me.steffenjacobs.iotplatformintegrator.domain.manage;

/** @author Steffen Jacobs */
public enum DiffType {
	FULL, FULL_DELETED, DESCRIPTION_CHANGED, LABEL_CHANGED, //
	TRIGGER_TYPE_CHANGE, TRIGGER_TYPE_VALUE_ADDED, TRIGGER_TYPE_VALUE_UPDATED, TRIGGER_TYPE_VALUE_DELETED, //
	CONDITION_TYPE_CHANGED, CONDITION_TYPE_VALUE_DELETED, CONDITION_TYPE_VALUE_UPDATED, CONDITION_TYPE_VALUE_ADDED, //
	ACTION_TYPE_CHANGED, ACTION_TYPE_VALUE_ADDED, ACTION_TYPE_VALUE_UPDATED, ACTION_TYPE_VALUE_DELETED
}
