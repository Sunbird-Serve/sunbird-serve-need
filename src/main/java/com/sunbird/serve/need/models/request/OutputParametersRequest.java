package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.enums.SoftwarePlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.time.Instant;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputParametersRequest {
    private String needDeliverableId;
    private String numberOfAttendees;
    private String submittedUrl;
    private String remarks;
}