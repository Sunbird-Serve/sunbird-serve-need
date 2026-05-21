package com.sunbird.serve.need.models.request;

import com.sunbird.serve.need.models.dto.TimeSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleRequest {
    private String days;
    private List<TimeSlotDTO> timeSlots;
}
