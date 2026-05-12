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
public class OutputParametersDTO implements Serializable {
    private Integer numberOfAttendees;
    private String submittedUrl;
    private String remarks;
}
