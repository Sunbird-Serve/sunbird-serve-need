package com.sunbird.serve.need.config;

/**
 * Thread-local holder for the authenticated user's tenant context.
 *
 * Populated by JwtTenantFilter on every request from the Keycloak JWT claims.
 * Services call TenantContext.getAgencyId() directly — no headers needed.
 *
 * Note: The old x-agency-id header approach has been replaced by JWT-based
 * tenant extraction. Agency context is now always derived from the verified token.
 */
public class TenantContext {

    private static final ThreadLocal<String> agencyId = new ThreadLocal<>();
    private static final ThreadLocal<String> agencyType = new ThreadLocal<>();
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> userEmail = new ThreadLocal<>();

    // --- Setters (called by JwtTenantFilter) ---

    public static void setAgencyId(String id) {
        agencyId.set(id);
    }

    public static void setAgencyType(String type) {
        agencyType.set(type);
    }

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static void setUserEmail(String email) {
        userEmail.set(email);
    }

    // --- Getters (called by services and controllers) ---

    public static String getAgencyId() {
        return agencyId.get();
    }

    public static String getAgencyType() {
        return agencyType.get();
    }

    public static String getUserId() {
        return userId.get();
    }

    public static String getUserEmail() {
        return userEmail.get();
    }

    /**
     * Returns true if the current request is scoped to a specific agency.
     * sAdmin requests may have no agencyId (platform-wide access).
     */
    public static boolean isTenantScoped() {
        String id = agencyId.get();
        return id != null && !id.isBlank();
    }

    /**
     * Must be called at the end of every request to prevent ThreadLocal leaks
     * in thread-pooled environments (Tomcat). Called by JwtTenantFilter.
     */
    public static void clear() {
        agencyId.remove();
        agencyType.remove();
        userId.remove();
        userEmail.remove();
    }
}
