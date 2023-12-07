package com.sunbird.serve.need.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceRequest {

    private Instant startDate;
    private Instant endDate;

    private String days;
    private String frequency;
    private List<TimeSlotRequest> timeSlots;
}
