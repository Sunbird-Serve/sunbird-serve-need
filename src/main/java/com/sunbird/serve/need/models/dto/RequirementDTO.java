package com.sunbird.serve.need.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementDTO implements Serializable {
    private String volunteersRequired;
    private String priority;
    private String skillDetails;
    private ScheduleDTO schedule;
}
