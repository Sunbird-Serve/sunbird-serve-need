package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardReviewRequest {
    private String action; // Authorise, Clarification, Reject
    private String notes;
    // userId is no longer required for Authorise — the backend creates the Keycloak user
    // and derives the userId automatically. Kept here for backward compatibility only.
    @Deprecated
    private String userId;
}
