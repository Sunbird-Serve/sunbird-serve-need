package com.sunbird.serve.need.models.response;

import com.sunbird.serve.need.models.Need.NeedPlan;
import com.sunbird.serve.need.models.Need.Occurrence;
import com.sunbird.serve.need.models.Need.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeedPlanResponse {
    private NeedPlan plan;
    private Occurrence occurrence;
    private List<TimeSlot> timeSlots;
}
