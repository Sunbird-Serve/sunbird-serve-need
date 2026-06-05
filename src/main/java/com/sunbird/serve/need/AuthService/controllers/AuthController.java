package com.sunbird.serve.need.AuthService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auth controller for the Keycloak POC.
 * Returns the authenticated user's context extracted from the JWT.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> userContext = new HashMap<>();

        userContext.put("username", jwt.getClaimAsString("preferred_username"));
        userContext.put("email", jwt.getClaimAsString("email"));
        userContext.put("agencyId", jwt.getClaimAsString("agencyId"));
        userContext.put("agencyType", jwt.getClaimAsString("agencyType"));

        // Get roles from the top-level "roles" claim
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            userContext.put("roles", roles);
        } else {
            // Fallback to realm_access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                userContext.put("roles", realmAccess.get("roles"));
            }
        }

        return ResponseEntity.ok(userContext);
    }
}
