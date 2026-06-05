package com.sunbird.serve.need.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts tenant context (agencyId, agencyType, userId, email) from the
 * validated JWT and populates TenantContext for the current request thread.
 *
 * Runs after Spring Security's authentication filter, so the JWT is already
 * validated when this filter executes.
 */
@Component
public class JwtTenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();

                TenantContext.setAgencyId(jwt.getClaimAsString("agencyId"));
                TenantContext.setAgencyType(jwt.getClaimAsString("agencyType"));
                TenantContext.setUserId(jwt.getSubject());
                TenantContext.setUserEmail(jwt.getClaimAsString("email"));
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear to prevent ThreadLocal leaks
            TenantContext.clear();
        }
    }
}
