package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInputParametersRequest {
    private String needDeliverableId;
    private Instant startTime;
    private Instant endTime;
} 