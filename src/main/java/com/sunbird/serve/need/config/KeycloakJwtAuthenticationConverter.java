package com.sunbird.serve.need.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converts Keycloak JWT claims into Spring Security authorities.
 * 
 * Extracts roles from both:
 * - "roles" (top-level claim from our custom protocol mapper)
 * - "realm_access.roles" (standard Keycloak claim)
 * 
 * Maps each role to a ROLE_ prefixed GrantedAuthority for use with
 * @PreAuthorize("hasRole('nAdmin')") etc.
 */
@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Extract from top-level "roles" claim (our custom mapper)
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        // Fallback: extract from "realm_access.roles" (standard Keycloak structure)
        if (authorities.isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                Object realmRoles = realmAccess.get("roles");
                if (realmRoles instanceof List<?>) {
                    for (Object role : (List<?>) realmRoles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
                    }
                }
            }
        }

        return authorities;
    }
}
