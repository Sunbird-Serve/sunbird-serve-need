package com.sunbird.serve.need.models.response;

import com.sunbird.serve.need.models.enums.OnboardRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardStatusResponse {
    private UUID requestId;
    private String entityName;
    private OnboardRequestStatus status;
    private String reviewerNotes;
}
