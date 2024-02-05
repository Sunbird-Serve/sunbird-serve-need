package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNeedTypeRequest {

    private NeedTypeRequest needTypeRequest;
    private NeedRequirementRequest needRequirementRequest;
}
