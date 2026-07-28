package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardRequest {
    private String agencyId;
    private UUID entityId;
    private String coordinatorName;
    private String mobile;
    private String email;
    private String designation;
    private Map<String, Object> infraDetails;
}
