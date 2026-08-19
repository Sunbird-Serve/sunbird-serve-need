package com.sunbird.serve.need.models.enums;

/**
 * Controls cross-agency visibility for needs and volunteers.
 * 
 * all      — visible to all agencies
 * selected — visible only to explicitly listed agencies
 * none     — visible only within the owning agency
 */
public enum VisibilityScope {
    all,
    selected,
    none
}
