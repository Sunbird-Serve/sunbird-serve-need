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
public class InputParametersDTO implements Serializable {
    private String inputUrl;
    private String softwarePlatform;
    private List<TimeSlotDTO> timeSlots;
}
