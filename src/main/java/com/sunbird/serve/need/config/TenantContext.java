package com.sunbird.serve.need.config;

import java.util.Map;

/**
 * Utility for extracting tenant (agency) context from request headers.
 * 
 * Usage in services:
 *   String agencyId = TenantContext.getAgencyId(headers);
 *   if (agencyId != null) { // scoped query } else { // unscoped (sAdmin) }
 */
public class TenantContext {

    private static final String AGENCY_HEADER = "x-agency-id";

    /**
     * Extracts agencyId from request headers.
     * Returns null if header is absent (indicates sAdmin / no tenant scoping).
     */
    public static String getAgencyId(Map<String, String> headers) {
        if (headers == null) return null;
        String agencyId = headers.get(AGENCY_HEADER);
        if (agencyId == null || agencyId.isBlank()) return null;
        return agencyId;
    }

    /**
     * Returns true if the request is tenant-scoped (has agency header).
     */
    public static boolean isTenantScoped(Map<String, String> headers) {
        return getAgencyId(headers) != null;
    }
}
