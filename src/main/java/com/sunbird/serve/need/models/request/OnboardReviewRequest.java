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
    private String userId; // Required for Authorise — the RC user osid, provided by UI after creating the user in volunteering service
}
