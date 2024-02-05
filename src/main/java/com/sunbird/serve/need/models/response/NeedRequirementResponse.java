package com.sunbird.serve.need.models.response;

import com.sunbird.serve.need.models.Need.NeedRequirement;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeedRequirementResponse {
    private Optional<NeedRequirement> needRequirement;
    private Optional<Occurrence> occurrence;
    private List<TimeSlot> timeSlots;
}
