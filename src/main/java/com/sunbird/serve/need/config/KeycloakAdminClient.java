package com.sunbird.serve.need.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Thin client for the Keycloak Admin REST API.
 *
 * Responsibilities (called during coordinator onboarding):
 *   1. Obtain a short-lived admin token via client_credentials
 *   2. Create a Keycloak user (username = mobile, email from onboard form)
 *   3. Set a temporary password (mobile number) — user must reset on first login
 *   4. Assign the nCoordinator realm role to the new user
 *   5. Add the user to the agency's Keycloak group (so agencyId flows into JWT)
 *
 * Config (application.properties / env vars):
 *   keycloak.admin.base-url          e.g. http://localhost:8080
 *   keycloak.admin.realm             e.g. sunbird-serve
 *   keycloak.admin.client-id         service-account client id
 *   keycloak.admin.client-secret     service-account client secret
 */
@Slf4j
@Component
public class KeycloakAdminClient {

    private final WebClient webClient;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String tokenEndpoint;

    public KeycloakAdminClient(
            WebClient.Builder webClientBuilder,
            @Value("${keycloak.admin.base-url}") String baseUrl,
            @Value("${keycloak.admin.realm}") String realm,
            @Value("${keycloak.admin.client-id}") String clientId,
            @Value("${keycloak.admin.client-secret}") String clientSecret) {

        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenEndpoint = "/realms/" + realm + "/protocol/openid-connect/token";
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Create a Keycloak user and fully provision them as nCoordinator.
     *
     * @param mobile      used as username and temporary password
     * @param email       coordinator's email (may be null)
     * @param name        coordinator's display name
     * @param agencyId    the agency's Keycloak group name (convention: agency-<agencyId>)
     * @return the new Keycloak user id (UUID string)
     */
    public String provisionCoordinator(String mobile, String email, String name, String agencyId) {
        String adminToken = fetchAdminToken();

        // 1. Create user
        String userId = createUser(adminToken, mobile, email, name);
        log.info("Keycloak user created: userId={}, mobile={}", userId, mobile);

        // 2. Set temporary password (mobile number — user must reset on first login)
        setTemporaryPassword(adminToken, userId, mobile);
        log.info("Temporary password set for userId={}", userId);

        // 3. Assign nCoordinator role
        assignRealmRole(adminToken, userId, "nCoordinator");
        log.info("Role nCoordinator assigned to userId={}", userId);

        // 4. Add to agency group (best-effort — group may not exist yet)
        if (agencyId != null && !agencyId.isBlank()) {
            try {
                String groupId = findGroupByName(adminToken, "agency-" + agencyId);
                addUserToGroup(adminToken, userId, groupId);
                log.info("User {} added to group agency-{}", userId, agencyId);
            } catch (Exception e) {
                // Non-fatal: group may not exist. Admin can add manually.
                log.warn("Could not add user {} to agency group for agencyId={}: {}", userId, agencyId, e.getMessage());
            }
        }

        return userId;
    }

    // -------------------------------------------------------------------------
    // Internal steps
    // -------------------------------------------------------------------------

    /** Fetch a short-lived admin token via client_credentials grant. */
    private String fetchAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        TokenResponse response = webClient.post()
                .uri(tokenEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to fetch admin token: " + body)))
                .bodyToMono(TokenResponse.class)
                .block();

        if (response == null || response.getAccessToken() == null) {
            throw new RuntimeException("Keycloak admin token response was null");
        }
        return response.getAccessToken();
    }

    /**
     * Create the user and return the new user's Keycloak id.
     * Keycloak returns 201 with Location header containing the user id.
     */
    private String createUser(String adminToken, String mobile, String email, String name) {
        Map<String, Object> userRepresentation = Map.of(
                "username", mobile,
                "email", email != null ? email : "",
                "firstName", name,
                "lastName", "",
                "enabled", true,
                "emailVerified", false
        );

        String location = webClient.post()
                .uri("/admin/realms/" + realm + "/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRepresentation)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to create Keycloak user: " + body)))
                .toBodilessEntity()
                .map(res -> {
                    var loc = res.getHeaders().getLocation();
                    if (loc == null) throw new RuntimeException("Keycloak did not return Location header after user creation");
                    return loc.toString();
                })
                .block();

        if (location == null) {
            throw new RuntimeException("Location header missing from Keycloak user creation response");
        }
        // Location is like: http://host/admin/realms/sunbird-serve/users/<uuid>
        return location.substring(location.lastIndexOf('/') + 1);
    }

    /** Set a temporary password — Keycloak will force a reset on first login. */
    private void setTemporaryPassword(String adminToken, String userId, String tempPassword) {
        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", tempPassword,
                "temporary", true
        );

        webClient.put()
                .uri("/admin/realms/" + realm + "/users/" + userId + "/reset-password")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(credential)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to set temporary password: " + body)))
                .toBodilessEntity()
                .block();
    }

    /** Assign a realm-level role to a user by role name. */
    private void assignRealmRole(String adminToken, String userId, String roleName) {
        // Step 1: look up the role representation (we need its id)
        RoleRepresentation role = webClient.get()
                .uri("/admin/realms/" + realm + "/roles/" + roleName)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Role '" + roleName + "' not found in Keycloak: " + body)))
                .bodyToMono(RoleRepresentation.class)
                .block();

        if (role == null) {
            throw new RuntimeException("Role representation null for: " + roleName);
        }

        // Step 2: assign role to user
        webClient.post()
                .uri("/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(role))
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to assign role: " + body)))
                .toBodilessEntity()
                .block();
    }

    /** Find a Keycloak group by exact name and return its id. */
    private String findGroupByName(String adminToken, String groupName) {
        List<GroupRepresentation> groups = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/" + realm + "/groups")
                        .queryParam("search", groupName)
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to search groups: " + body)))
                .bodyToFlux(GroupRepresentation.class)
                .collectList()
                .block();

        if (groups == null || groups.isEmpty()) {
            throw new RuntimeException("Keycloak group not found: " + groupName);
        }
        return groups.stream()
                .filter(g -> groupName.equals(g.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No exact match for group: " + groupName))
                .getId();
    }

    /** Add a user to a group. */
    private void addUserToGroup(String adminToken, String userId, String groupId) {
        webClient.put()
                .uri("/admin/realms/" + realm + "/users/" + userId + "/groups/" + groupId)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Failed to add user to group: " + body)))
                .toBodilessEntity()
                .block();
    }

    // -------------------------------------------------------------------------
    // Response types
    // -------------------------------------------------------------------------

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RoleRepresentation {
        private String id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GroupRepresentation {
        private String id;
        private String name;
    }
}
