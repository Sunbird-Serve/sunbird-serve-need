package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.NeedTypeStatus;
import com.sunbird.serve.need.models.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeedTypeRequest {
    private String description;
    private String userId;
    private String taxonomyId;
    private String onboardingId;
    private String name;
    private NeedTypeStatus status;
    private TaskType taskType;
}
