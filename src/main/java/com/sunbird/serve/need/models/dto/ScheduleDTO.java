package com.sunbird.serve.need.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO implements Serializable {
    private String startDate;
    private String endDate;
    private String frequency;
    private String days;
    private List<TimeSlotDTO> timeSlots;
}
