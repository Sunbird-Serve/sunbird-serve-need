package com.sunbird.serve.need.models.response;

import com.sunbird.serve.need.models.Need.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeedEntityAndRequirement {
    private Need need;
    private Optional<NeedRequirement> needRequirement;
    private Optional<Occurrence> occurrence;
    private List<TimeSlot> timeSlots;
    private Optional<NeedType> needType;
}

